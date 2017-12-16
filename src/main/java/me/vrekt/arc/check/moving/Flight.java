package me.vrekt.arc.check.moving;

import me.vrekt.arc.Arc;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckResult;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.data.moving.VelocityData;
import me.vrekt.arc.utilties.LocationHelper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class Flight extends Check {

    private double maxAscendSpeed, maxDescendSpeed, maxHeight = 0.0;
    private int maxAscendTime = 0;

    public Flight() {
        super(CheckType.FLIGHT);

        maxAscendSpeed = Arc.getCheckManager().getValueDouble(CheckType.FLIGHT, "ascend-ladder");
        maxDescendSpeed = Arc.getCheckManager().getValueDouble(CheckType.FLIGHT, "descend-ladder");
        maxHeight = Arc.getCheckManager().getValueDouble(CheckType.FLIGHT, "max-jump");

        maxAscendTime = Arc.getCheckManager().getValueInt(CheckType.FLIGHT, "ascend-time");
    }

    private boolean hoverCheck(Player player, MovingData data) {
        result.reset();
        if (data.isOnGround()) {
            data.setAirTicks(0);
        }

        // Check if we are actually hovering.
        double vertical = data.getVerticalSpeed();
        boolean actuallyHovering = data.getLastVerticalSpeed() == 0.0 && vertical == 0.0 && player.getVehicle() == null;

        // TODO: Calculate if we're climbing only once.
        if (actuallyHovering) {
            // check how long we've been hovering for.
            if (data.getAirTicks() >= 10) {
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
        int ascendingMoves = data.getAscendingMoves();

        // make sure we're actually in the air.
        boolean actuallyInAir = airTicks >= 20 && ascendingMoves > 4 && player.getFallDistance() == 0.0;

        // fastladder check, make sure were ascending, climbing and in-air.
        if (isAscending && isClimbing && actuallyInAir) {
            // check if we are climbing too fast.
            if (vertical > maxAscendSpeed) {
                getCheck().setCheckName("Flight " + ChatColor.GRAY + "(Ladder)");
                result.set(checkViolation(player, "ascending too fast, ladder_ascend"), data.getPreviousLocation());
            }

            // patch for instant ladder
            if (vertical > maxAscendSpeed + 0.12) {
                getCheck().setCheckName("Flight " + ChatColor.GRAY + "(Ladder)");
                result.set(checkViolation(player, "ascending too fast, ladder_instant"), data.getPreviousLocation());
            }

        }

        // descending check.
        if (isDescending && isClimbing && actuallyInAir) {
            // too fast, flag.
            if (vertical > maxDescendSpeed) {
                getCheck().setCheckName("Flight " + ChatColor.GRAY + "(Ladder)");
                result.set(checkViolation(player, "descending too fast, ladder_descend"), data.getPreviousLocation());
            }
        }

        getCheck().setCheckName("Flight");
        return result;
    }

    public CheckResult check(Player player, MovingData data) {
        result.reset();

        Location ground = data.getGroundLocation();
        Location from = data.getPreviousLocation();
        Location to = data.getCurrentLocation();

        boolean isAscending = data.isAscending();
        boolean isDescending = data.isDescending();
        boolean isClimbing = data.isClimbing();

        boolean velocityModifier = LocationHelper.isOnSlab(to) || LocationHelper.isOnStair(to);

        boolean inLiquid = LocationHelper.isInLiquid(to);
        boolean onGround = data.isOnGround();

        double vertical = data.getVerticalSpeed();
        boolean hasSlimeblock = LocationHelper.isOnSlimeblock(to);

        boolean hasVelocity = data.getVelocityData().hasVelocity();
        double velocity = data.getVelocityData().getCurrentVelocity();
        int ascendingMoves = data.getAscendingMoves();

        if (onGround) {
            // reset data.
            data.setAscendingMoves(0);
            data.setDescendingMoves(0);

            if (hasSlimeblock) {
                // update velocity stuff.
                data.getVelocityData().setVelocityCause(VelocityData.VelocityCause.SLIMEBLOCK);
                data.getVelocityData().setHasVelocity(true);
            }
            hasVelocity = data.getVelocityData().hasVelocity();

        }

        if (isAscending) {
            data.setDescendingMoves(0);
            if (!isClimbing) {
                ascendingMoves += 1;
                data.setAscendingMoves(ascendingMoves);
            }
        }

        if (isDescending) {
            data.setAscendingMoves(0);
            data.getVelocityData().setHasVelocity(false);
        }

        // Update ladder data.
        if (isClimbing) {
            if (isDescending || isAscending) {
                data.setLadderTime(8);
            } else {
                data.setLadderTime(4);
            }
        } else {
            int ladderTime = data.getLadderTime();
            data.setLadderTime(ladderTime > 0 ? ladderTime - 1 : 0);
        }


        // Calculate if we have slimeblock velocity and if we are actually ascending/descending.
        boolean hasSlimeblockVelocity = hasVelocity && data.getVelocityData().getVelocityCause().equals(VelocityData.VelocityCause
                .SLIMEBLOCK) && !isClimbing && !inLiquid;
        boolean hasActualVelocity = !isClimbing && !inLiquid && player.getVehicle() == null &&
                !velocityModifier && !hasVelocity;

        // reset knockback data.
        if (hasVelocity) {
            if (data.getVelocityData().getVelocityCause().equals(VelocityData.VelocityCause.KNOCKBACK)) {
                data.getVelocityData().setHasVelocity(false);
                return result;
            }
        }

        if ((isAscending || isDescending) && !hasVelocity) {
            // we made a pretty big move, lets check where they went.
            if (vertical > 0.99) {
                // first update our safe location.
                Location safe = data.getSafe();
                if (safe == null) {
                    data.setSafe(from);
                    safe = from;
                }

                // get our locations for checking.
                int fromY = safe.getBlockY();
                int toY = safe.getBlockY() + 1;

                int minY = Math.min(safe.getBlockY(), to.getBlockY());
                int maxY = Math.max(safe.getBlockY(), to.getBlockY());

                for (int yy = fromY; yy <= toY; yy++) {
                    for (int y = minY; y <= maxY; y++) {
                        // loop through all the possible y coordinates and get the block at that location.
                        Block blockFrom = to.getWorld().getBlockAt(to.getBlockX(), yy, to.getBlockZ());
                        Block blockTo = to.getWorld().getBlockAt(to.getBlockX(), y, to.getBlockZ());
                        if (blockFrom.getType().isSolid() || blockTo.getType().isSolid()) {
                            // if its solid flag.
                            getCheck().setCheckName("Flight " + ChatColor.GRAY + "(Vertical Clip)");
                            boolean failed = checkViolation(player, "clipped through a solid block, vclip_solid");
                            result.set(failed, safe);
                        }

                    }
                }

                // return right away, lets cancel this first.
                if (result.failed()) {
                    return result;
                }
            }
        }

        data.setSafe(from);

        if (!onGround && hasSlimeblockVelocity) {
            // make sure we're not ascending too high by checking if our velocity goes down.
            double last = data.getVelocityData().getLastVelocity();
            if (velocity > last && ascendingMoves > maxAscendTime) {
                // were ascending too high, flag.
                getCheck().setCheckName("Flight " + ChatColor.GRAY + "(Ascension)");
                result.set(checkViolation(player, "ascending too high, ascending_slimeblock"));
            }

        }

        // check for jesus.
        if ((isAscending || isDescending) && inLiquid && !onGround) {
            boolean ccGround = data.isPositionClientOnGround();
            if (ccGround && vertical != 0.0) {
                getCheck().setCheckName("Flight " + ChatColor.GRAY + "(Jesus)");
                result.set(checkViolation(player, "walking on water, ccground_liquid"));
            }
        }

        // Make sure we're not jumping too high or for too long.
        boolean hasFence = LocationHelper.walkedOnFence(to);
        if (hasActualVelocity && isAscending && !hasFence) {
            double distance = LocationHelper.distanceVertical(ground, to);
            // distance is pretty high, that's not right.
            if (distance >= 1.4) {
                getCheck().setCheckName("Flight " + ChatColor.GRAY + "(Ascension)");
                result.set(checkViolation(player, "ascending too high, ascending_distance"));
            }

            // check ascend time.
            if (ascendingMoves > maxAscendTime) {
                // too long, flag.
                getCheck().setCheckName("Flight " + ChatColor.GRAY + "(Ascension)");
                result.set(checkViolation(player, "ascending for too long, ascending_move"));
            }

            // jumping too high
            if (vertical > getMaxJump(player)) {
                getCheck().setCheckName("Flight " + ChatColor.GRAY + "(Ascension)");
                result.set(checkViolation(player, "vertical too high, vertical_jump"), from);
            }

        }

        // make sure were actually falling.
        if (hasActualVelocity && isDescending) {
            int descendMoves = data.getDescendingMoves() + 1;
            data.setDescendingMoves(descendMoves);

            double lastVertical = data.getLastVerticalSpeed();
            double glideDelta = Math.abs(vertical - lastVertical);

            // were descending at the same speed, that isnt right.
            if (glideDelta == 0.0) {
                getCheck().setCheckName("Flight " + ChatColor.GRAY + "(Glide)");
                result.set(checkViolation(player, "vertical not changing, descend_delta"));
            }
            double glideDifference = vertical - lastVertical;
            double distance = LocationHelper.distanceVertical(ground, to);

            // TODO: improve later, could probably be bypassed.
            if (distance > 1.6 && data.getLadderTime() == 0) {
                if (glideDifference > 0.07 || glideDifference < 0.05) {
                    getCheck().setCheckName("Flight " + ChatColor.GRAY + "(Glide)");
                    result.set(checkViolation(player, "vertical not changing, descend_difference"));
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
