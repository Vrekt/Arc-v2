package me.vrekt.arc.check.moving;

import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.utilties.LocationHelper;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Speed extends Check {

    public Speed() {
        super(CheckType.SPEED);
    }

    public boolean check(Player player, MovingData data) {
        result.reset();
        Location from = data.getPreviousLocation();
        Location to = data.getCurrentLocation();

        boolean velocityModifier = LocationHelper.isOnSlab(to) || LocationHelper.isOnStair(to);
        boolean onGround = data.isOnGround();
        int groundTicks = data.getGroundTime();

        double thisMove = LocationHelper.distanceHorizontal(from, to);
        double baseMove = getBaseMoveSpeed(player);
        double vertical = data.getVerticalSpeed();

        // handle groundTick stuff.
        if (onGround) {
            groundTicks++;
            data.setGroundTime(groundTicks >= 8 ? 8 : groundTicks);
        }

        if (!onGround) {
            data.setGroundTime(0);
            groundTicks = 0;
        }

        if (onGround && !velocityModifier) {
            // Check if we have a block above us.
            boolean hasBlock = LocationHelper.isUnderBlock(to);
            boolean hasIce = LocationHelper.isOnIce(to);

            // modify ground time.
            if (hasBlock) {
                if (vertical > 0.0) {
                    groundTicks = hasIce ? 0 : 2;
                    data.setGroundTime(groundTicks);
                }
            }

            // this does not account for jumping.
            double expected = thisMove / data.getGroundTime() + baseMove;

            if (hasBlock) {
                // TODO: Instead of magic values try to calculate an expected.
                // we have a block, check if we are jumping.
                if (vertical > 0.0) {
                    // check if we are on ice.
                    if (hasIce) {
                        // we have ice and we are jumping lets adjust and check.
                        double iceExpected = 0.913;
                        if (thisMove > iceExpected) {
                            result.set(checkViolation(player, "Moving too fast, onground_ice_block m=" + thisMove + " e=" + iceExpected));
                        }
                    } else {
                        // no ice and jumping.
                        double jumpingExpected = 0.6699;
                        if (thisMove > jumpingExpected) {
                            result.set(checkViolation(player, "Moving too fast, onground_block m=" + thisMove + " e=" + jumpingExpected));
                        }
                    }
                }
            }

            // make sure we've actually been onGround, without block jumps, etc.
            if (groundTicks >= 5) {
                // no block, normal check.
                if (thisMove > expected) {
                    result.set(checkViolation(player, "Moving too fast, onground_expected m=" + thisMove + " e=" + expected));
                }

            }
        }

        if (!onGround) {
            boolean isOnIce = LocationHelper.isOnIce(to);
            int iceTime = data.getIceTime();
            if (!isOnIce) {
                data.setIceTime(iceTime > 0 ? iceTime - 1 : 0);
            }

            // check if we have been 'launched' by ice.
            if (isOnIce) {
                data.setIceTime(8);
                double iceExpected = 0.58;
                if (thisMove > iceExpected) {
                    result.set(checkViolation(player, "Moving too fast, offground_ice m=" + thisMove + " e=" + iceExpected));
                }
            }

            // normal checks no ice.
            if (!isOnIce && iceTime == 0) {
                double stageExpected = vertical > 0.26 && vertical < 0.40 ? 0.017 : vertical < 0.26 ? 0.026 : vertical >= 0.41 ? 0.163 :
                        0.03;
                double expected = thisMove / data.getAirTicks() + (baseMove + (vertical * baseMove)) + stageExpected;
                if (Double.isInfinite(expected)) {
                    // return, we dont have the required data yet.
                    return false;
                }

                if (thisMove > expected) {
                    result.set(checkViolation(player, "Moving too fast, offground m=" + thisMove + " e=" + expected + " stage=" +
                            stageExpected));
                }
            }
        }

        // if we didnt fail set our setback.
        if (!result.failed()) {
            data.setSetback(from);
        }

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
