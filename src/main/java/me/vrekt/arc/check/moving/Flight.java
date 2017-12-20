package me.vrekt.arc.check.moving;

import me.vrekt.arc.Arc;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckResult;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.utilties.LocationHelper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.material.Step;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Flight extends Check {

    private double maxAscendSpeed, maxDescendSpeed, maxHeight, maxHover, maxAscendDistance;
    private int maxAscendTime;

    public Flight() {
        super(CheckType.FLIGHT);

        maxAscendSpeed = Arc.getCheckManager().getValueDouble(CheckType.FLIGHT, "ascend-ladder");
        maxDescendSpeed = Arc.getCheckManager().getValueDouble(CheckType.FLIGHT, "descend-ladder");
        maxHeight = Arc.getCheckManager().getValueDouble(CheckType.FLIGHT, "max-jump");
        maxHover = Arc.getCheckManager().getValueInt(CheckType.FLIGHT, "max-hover-time");
        maxAscendDistance = Arc.getCheckManager().getValueDouble(CheckType.FLIGHT, "ascend-distance");

        maxAscendTime = Arc.getCheckManager().getValueInt(CheckType.FLIGHT, "ascend-time");
    }

    private boolean hoverCheck(Player player, MovingData data) {
        result.reset();

        // Check if we are actually hovering.
        double vertical = data.getVerticalSpeed();
        boolean actuallyHovering = data.getLastVerticalSpeed() == 0.0 && vertical == 0.0 && player.getVehicle() == null;

        if (actuallyHovering) {
            // check how long we've been hovering for.
            if (data.getAirTicks() >= maxHover) {
                // too long, flag.
                getCheck().setCheckName("Flight " + ChatColor.GRAY + "(Hover)");
                result.set(checkViolation(player, "hovering off the ground, hover"));
            }
        }

        return result.failed();
    }

    public CheckResult runBlockChecks(Player player, MovingData data) {
        result.reset();

        if (!data.wasOnGround()) {
            result.set(hoverCheck(player, data));
        }

        double vertical = data.getVerticalSpeed();

        // ladder and vertical velocity stuff.
        boolean isAscending = data.isAscending();
        boolean isDescending = data.isDescending();
        boolean isClimbing = data.isClimbing();

        int airTicks = data.getAirTicks();

        // make sure we're actually in the air.
        boolean actuallyInAir = airTicks >= 20 && data.getLadderTime() >= 8;

        // fastladder check, make sure were ascending, climbing and in-air.
        if (isAscending && isClimbing && actuallyInAir) {
            // check if we are climbing too fast.
            if (vertical > maxAscendSpeed) {
                getCheck().setCheckName("Flight " + ChatColor.GRAY + "(FastLadder)");
                result.set(checkViolation(player, "ascending too fast, ladder_ascend"), data.getPreviousLocation());
            }
        }

        // patch for instant ladder
        double difference = vertical - maxAscendSpeed;
        if (isAscending && isClimbing && difference > 1.0) {
            getCheck().setCheckName("Flight " + ChatColor.GRAY + "(FastLadder)");
            result.set(checkViolation(player, "ascending too fast, ladder_instant"), data.getPreviousLocation());
        }

        // descending check.
        if (isDescending && isClimbing && actuallyInAir) {
            // too fast, flag.
            if (vertical > maxDescendSpeed) {
                getCheck().setCheckName("Flight " + ChatColor.GRAY + "(FastLadder)");
                result.set(checkViolation(player, "descending too fast, ladder_descend"), data.getPreviousLocation());
            }
        }

        getCheck().setCheckName("Flight");
        return result;
    }

    public CheckResult check(Player player, MovingData data) {
        result.reset();

        Location from = data.getPreviousLocation();
        Location to = data.getCurrentLocation();
        Location ground = data.getGroundLocation();
        if (ground == null) {
            ground = from;
        }


        double lastVertical = data.getLastVerticalSpeed();
        double vertical = data.getVerticalSpeed();

        boolean onGround = data.isOnGround();
        boolean isAscending = data.isAscending();
        boolean isDescending = data.isDescending();
        boolean isClimbing = data.isClimbing();

        boolean isOnSlab = to.getBlock().getRelative(BlockFace.DOWN).getType().getData().equals(Step.class);
        boolean isOnStep = (LocationHelper.isOnSlab(to) || isOnSlab) || LocationHelper.isOnStair(to);
        boolean isOnSlimeblock = LocationHelper.isOnSlimeblock(to);
        boolean isInLiquid = LocationHelper.isInLiquid(to);
        boolean isOnTrapdoor = to.getBlock().getRelative(BlockFace.SELF).getType() == Material.TRAP_DOOR;
        boolean isUnderBlock = LocationHelper.isUnderBlock(to);
        boolean velocityBoost = isOnTrapdoor && isUnderBlock;

        boolean hasVelocity = data.getVelocityData().hasVelocity();
        double velocity = data.getVelocityData().getCurrentVelocity();
        double lastVelocity = data.getVelocityData().getLastVelocity();

        boolean wasVerticalMove = (isAscending || isDescending);
        boolean validVerticalMove = player.getVehicle() == null && !hasVelocity && !isOnStep && !isClimbing && !isInLiquid;

        int ascendingMoves = data.getAscendingMoves();
        int descendingMoves = data.getDescendingMoves();
        int ladderTime = data.getLadderTime();

        // if we are on ground lets reset some data.
        if (onGround) {
            ascendingMoves = 0;
            descendingMoves = 0;

            data.setAscendingMoves(ascendingMoves);
            data.setDescendingMoves(descendingMoves);
            data.getVelocityData().clear();

            if (hasVelocity) {
                hasVelocity = false;
                data.getVelocityData().setHasVelocity(false);
                validVerticalMove = wasVerticalMove && player.getVehicle() == null && !isOnStep;
            }

            if (isOnSlimeblock) {
                // we landed on a slimeblock, lets update our velocity if out vertical has increased.
                if (vertical > 0.0 && !data.wasOnGround()) {
                    data.getVelocityData().setHasVelocity(true);
                    hasVelocity = true;
                    validVerticalMove = false;
                }
            }

        }

        // update our ascending data and reset our descending data.
        if (isAscending) {
            descendingMoves = 0;
            data.setDescendingMoves(descendingMoves);
            if (!isClimbing) {
                ascendingMoves++;
                data.setAscendingMoves(ascendingMoves);
            }
        }

        // update our descending data and reset the ascending data, also account for slimeblock velocity.
        if (isDescending) {
            ascendingMoves = 0;
            data.setAscendingMoves(ascendingMoves);

            descendingMoves++;
            data.setDescendingMoves(descendingMoves);
            data.getVelocityData().clear();
        }

        // update our climbing data.
        if (isClimbing) {
            if (wasVerticalMove) {
                data.setLadderTime(14);
            } else {
                data.setLadderTime(6);
            }
        } else {
            ladderTime = ladderTime >= 0 ? ladderTime - 1 : 0;
            data.setLadderTime(ladderTime);
        }

        if (wasVerticalMove) {
            // start with the vclip check, we want this first to make sure nobody is clipping through anything.
            if (vertical >= 0.99 && validVerticalMove && !isOnSlimeblock) {
                // update our safe location.
                Location safeLocation = data.getSafe();
                if (safeLocation == null) {
                    data.setSafe(from);
                    safeLocation = from;
                }

                // get our locations for checking.
                int minY = Math.min(safeLocation.getBlockY(), to.getBlockY());
                int maxY = Math.max(safeLocation.getBlockY(), to.getBlockY() + 1);
                for (int y = minY; y <= maxY; y++) {
                    // loop through all the possible y coordinates and get the block at that location.
                    Block blockTo = to.getWorld().getBlockAt(to.getBlockX(), y, to.getBlockZ());
                    if (blockTo.getType().isSolid()) {
                        // if its solid flag.
                        getCheck().setCheckName("Flight " + ChatColor.GRAY + "(Vertical Clip)");
                        boolean failed = checkViolation(player, "clipped through a solid block, vclip_solid");
                        result.set(failed, safeLocation);
                    }
                }

            }

            // reset our safe location.
            if (!result.failed()) {
                data.setSafe(to);
            }


            if (!onGround) {
                if (hasVelocity && isAscending && ladderTime == 0) {
                    // we were on a slimeblock, watch our velocity.
                    data.getVelocityData().addVelocity(velocity);
                    if (data.getVelocityData().getSlimeblockVelocity().size() >= maxAscendTime) {
                        List<Double> velocityData = data.getVelocityData().getSlimeblockVelocity();

                        // get our "last" velocities.
                        double lastChange = Math.abs(velocity - lastVelocity);
                        double lastVelocityMove = lastVelocity;
                        int validMoves = 0;

                        // loop through all of the recent moves.
                        for (double move : velocityData) {
                            if (move < lastVelocityMove) {
                                // get the difference and add a valid move if we are decreasing in velocity.
                                double difference = Math.abs(move - lastVelocityMove);
                                if (difference < lastChange) {
                                    validMoves++;
                                }
                                lastChange = difference;
                            }
                            lastVelocityMove = move;
                        }

                        // no valid moves, flag.
                        if (validMoves <= 3) {
                            getCheck().setCheckName("Flight " + ChatColor.GRAY + "(Glide)");
                            result.set(checkViolation(player, "ascending too high after slimeblock velocity, sb_velocity_watch"));
                        }
                        data.getVelocityData().clear();
                    }
                }

                if (isInLiquid) {
                    // check if the client is onGround, if so flag.
                    boolean ccGround = data.isPositionClientOnGround();
                    if (ccGround && vertical > 0.0) {
                        getCheck().setCheckName("Flight " + ChatColor.GRAY + "(Jesus)");
                        result.set(checkViolation(player, "walking on water, ccground_liquid"));
                    }
                }

            }

            // check how we are ascending.
            boolean walkedOnFence = LocationHelper.walkedOnFence(to);
            double distanceFrom = LocationHelper.distanceVertical(ground, to);
            if (validVerticalMove && isAscending && !walkedOnFence && ladderTime == 0) {
                // valid move, check.
                // first get our distance we have traveled.
                if (distanceFrom >= maxAscendDistance) {
                    getCheck().setCheckName("Flight " + ChatColor.GRAY + "(Ascension)");
                    result.set(checkViolation(player, "ascending too high, ascending_distance"));
                }

                // ascending for too long.
                if (ascendingMoves > maxAscendTime) {
                    getCheck().setCheckName("Flight " + ChatColor.GRAY + "(Ascension)");
                    result.set(checkViolation(player, "ascending for too long, ascending_time"));
                }

                // ascending too fast.
                double max = getMaxJump(player);
                if (vertical > max) {
                    getCheck().setCheckName("Flight " + ChatColor.GRAY + "(Ascension)");
                    result.set(checkViolation(player, "Ascending too fast, ascending_vertical"));
                }

            }

            // check how we are descending.
            if (validVerticalMove && isDescending && ladderTime == 0 && !isOnSlimeblock && !velocityBoost) {
                double glideDelta = Math.abs(vertical - lastVertical);

                // check if we are descending at the same speed.
                if (glideDelta == 0.0) {
                    getCheck().setCheckName("Flight " + ChatColor.GRAY + "(Glide)");
                    result.set(checkViolation(player, "vertical not changing, descend_difference"));
                }

                // check the speed of how we are descending.
                int airTicks = data.getAirTicks();
                if (distanceFrom > 1.6 && descendingMoves > 2) {
                    double expected;
                    // calculate our expected difference based on air.
                    if (airTicks > 100) {
                        expected = -0.9908 * 1e-6 * Math.pow(airTicks, 2) + 3.4538 * 1e-7 * airTicks + 0.0189;
                    } else {
                        expected = 5.4246 * 1e-6 * Math.pow(airTicks, 2) - 0.0011 * airTicks + 0.0659;
                    }

                    double difference = Math.abs(glideDelta - expected);
                    if (difference > 0.02 || difference < 0.001) {
                        getCheck().setCheckName("Flight " + ChatColor.GRAY + "(Glide)");
                        result.set(checkViolation(player, "vertical not expected, descend_expected"));
                    }
                }

            }

        }

        getCheck().setCheckName("Flight");
        return result;
    }

    /**
     * Return max distance we can ascend.
     *
     * @param player the player
     * @return the max jump height.
     */
    private double getMaxJump(Player player) {

        double max = maxHeight;

        if (player.hasPotionEffect(PotionEffectType.JUMP)) {
            max += 0.4;
        }
        return max;

    }

}
