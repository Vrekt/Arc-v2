package me.vrekt.arc.check.moving;

import me.vrekt.arc.Arc;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.utilties.LocationHelper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.material.Step;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Speed extends Check {

    private double iceBlockMax, ice;
    private double blockMoveMax, stepMax;

    public Speed() {
        super(CheckType.SPEED);

        iceBlockMax = Arc.getCheckManager().getValueDouble(CheckType.SPEED, "ice_block");
        ice = Arc.getCheckManager().getValueDouble(CheckType.SPEED, "ice");
        blockMoveMax = Arc.getCheckManager().getValueDouble(CheckType.SPEED, "block");
        stepMax = Arc.getCheckManager().getValueDouble(CheckType.SPEED, "step_modifier");

    }

    public boolean check(Player player, MovingData data) {
        result.reset();
        Location from = data.getPreviousLocation();
        Location to = data.getCurrentLocation();

        boolean slab = to.getBlock().getRelative(BlockFace.DOWN).getType().getData().equals(Step.class);
        boolean velocityModifier = (LocationHelper.isOnSlab(to) || slab) || (LocationHelper.isOnStair(to) ||
                LocationHelper
                        .isOnStairJump(to));

        boolean onGround = data.isOnGround();
        int groundTicks = data.getGroundTime();

        double thisMove = LocationHelper.distanceHorizontal(from, to);
        double baseMove = getBaseMoveSpeed(player);
        double vertical = data.getVerticalSpeed();

        // cancel large movements.
        if (thisMove > 8) {
            return true;
        }

        // handle groundTick stuff.
        if (onGround) {
            groundTicks++;
            data.setGroundTime(groundTicks >= 8 ? 8 : groundTicks);
        }

        if (!onGround) {
            data.setGroundTime(0);
            groundTicks = 0;
        }

        // handle ice/slimeblock stuff.
        boolean isOnIce = LocationHelper.isOnIce(to);
        boolean isOnSlimeblock = LocationHelper.isOnSlimeblock(to);

        int iceTime = data.getIceTime();
        if (!isOnIce) {
            // update data.
            data.setIceTime(iceTime > 0 ? iceTime - 1 : 0);
        } else {
            data.setIceTime(8);
        }

        int slimeblockTime = data.getSlimeblockTime();
        if (!isOnSlimeblock) {
            // update data.
            data.setSlimeblockTime(slimeblockTime > 0 ? slimeblockTime - 1 : 0);
        } else {
            data.setSlimeblockTime(8);
        }

        if (onGround) {
            // Check if we have a block above us.
            boolean hasBlock = LocationHelper.isUnderBlock(to);

            // modify ground time.
            if (hasBlock) {
                if (vertical > 0.0) {
                    groundTicks = isOnIce ? 0 : 2;
                    data.setGroundTime(groundTicks);
                }
            }

            // this does not account for jumping.
            double expected = thisMove / data.getGroundTime() + baseMove;

            if (hasBlock) {
                // TODO: Instead of magic values try to calculate an expected?
                // we have a block, check if we are jumping.
                if (vertical > 0.0) {
                    // check if we are on ice.
                    if (isOnIce) {
                        // we have ice and we are jumping lets adjust and check.
                        if (thisMove > iceBlockMax) {
                            getCheck().setCheckName("Speed " + ChatColor.GRAY + "(Ice)");
                            result.set(checkViolation(player, "Moving too fast, onground_ice_block m=" + thisMove + " e=" + iceBlockMax));
                        }
                    }

                    if (!isOnIce && data.getIceTime() <= 3) {
                        // no ice and jumping.
                        if (thisMove > blockMoveMax) {
                            getCheck().setCheckName("Speed " + ChatColor.GRAY + "(BunnyHop)");
                            result.set(checkViolation(player, "Moving too fast, onground_block m=" + thisMove + " e=" + blockMoveMax));
                        }
                    }
                }
            }

            // make sure we've actually been onGround, without block jumps, etc.
            if (groundTicks >= 5) {
                boolean hadModifier = from.getBlock().getRelative(BlockFace.DOWN).getType().getData().equals(Step.class);
                double stepModifier = velocityModifier ? stepMax + expected : 0;
                // no block, normal check.
                if (velocityModifier || hadModifier) {
                    if (thisMove > stepModifier) {
                        getCheck().setCheckName("Speed " + ChatColor.GRAY + "(onGround)");
                        result.set(checkViolation(player, "Moving too fast, onground_expected_velocity m=" + thisMove + " e=" + stepModifier));
                    }
                } else {
                    if (vertical > 0.41) {
                        // we jumped but we're still onGround.
                        expected += vertical / thisMove;
                    }
                    if (thisMove > expected) {
                        getCheck().setCheckName("Speed " + ChatColor.GRAY + "(onGround)");
                        result.set(checkViolation(player, "Moving too fast, onground_expected m=" + thisMove + " e=" + expected));
                    }
                }

            }
        }

        if (!onGround) {
            // if we are on a slimeblock.
            if (isOnSlimeblock) {
                // get the jump stage
                double stage = vertical == 0.0 ? 0.1018 : vertical < 0.34 ? 0.1504 : 0.28;
                double expected = (baseMove + stage);
                if (thisMove > expected) {
                    getCheck().setCheckName("Speed " + ChatColor.GRAY + "(BunnyHop Slimeblock)");
                    result.set(checkViolation(player, "Moving too fast, offground_slime m=" + thisMove + " e=" + expected));
                }
            }

            if (isOnIce) {
                // check if we have been 'launched' by ice.
                if (thisMove > ice) {
                    getCheck().setCheckName("Speed " + ChatColor.GRAY + "(BunnyHop Ice)");
                    result.set(checkViolation(player, "Moving too fast, offground_ice m=" + thisMove + " e=" + ice));
                }

            }

            boolean hasModifier = (isOnIce || iceTime > 0) || (isOnSlimeblock || slimeblockTime > 0);
            if (!hasModifier) {
                // get the jump stage
                double stage = vertical == 0.0 ? 0.049 : vertical < 0.34 ? 0.08 : 0.3261;
                double expected = (baseMove + stage);

                // too fast, flag.
                if (thisMove > expected) {
                    getCheck().setCheckName("Speed " + ChatColor.GRAY + "(BunnyHop)");
                    result.set(checkViolation(player, "Moving too fast, offground_expected m=" + thisMove + " e=" + expected));
                }
            }

        }

        // if we didnt fail set our setback.
        if (!result.failed()) {
            data.setSetback(from);
        }

        getCheck().setCheckName("Speed");
        return result.failed();
    }

    /**
     * Return our base move speed.
     *
     * @param player the player
     * @return players move speed
     */
    private double getBaseMoveSpeed(Player player) {
        double baseSpeed = 0.2873;

        for (PotionEffect ef : player.getActivePotionEffects()) {
            if (ef.getType().equals(PotionEffectType.SPEED)) {
                baseSpeed *= 1.0 + 0.2 * ef.getAmplifier() + 1;
            }
        }

        return baseSpeed;
    }

}
