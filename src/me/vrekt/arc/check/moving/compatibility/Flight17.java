package me.vrekt.arc.check.moving.compatibility;

import me.vrekt.arc.Arc;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.utilties.LocationHelper;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class Flight17 extends Check {
    private double maxAscendSpeed, maxDescendSpeed, maxHeight = 0.0;
    private int maxAscendTime = 0;

    public Flight17() {
        super(CheckType.FLIGHT_17);

        maxAscendSpeed = Arc.getCheckManager().getValueDouble(CheckType.FLIGHT, "ascend-ladder");
        maxDescendSpeed = Arc.getCheckManager().getValueDouble(CheckType.FLIGHT, "descend-ladder");
        maxHeight = Arc.getCheckManager().getValueDouble(CheckType.FLIGHT, "max-jump");

        maxAscendTime = Arc.getCheckManager().getValueInt(CheckType.FLIGHT, "ascend-time");

    }

    public void hoverCheck(Player player, MovingData data) {
        if (data.isOnGround()) {
            data.setAirTicks(0);
        }

        // Check if we are actually hovering.
        double vertical = data.getVerticalSpeed();
        boolean actuallyHovering = data.getLastVerticalSpeed() == 0.0 && vertical == 0.0 && player.getVehicle() == null;

        if (actuallyHovering) {
            // check how long we've been hovering for.
            if (data.getAirTicks() >= 10) {
                // too long, flag.
                checkViolation(player, "airTicks more than allowed while no velocity is present. airTicks=" + data.getAirTicks());
                handleCheckCancel(player, data.getGroundLocation());
            }
        }

    }

    public boolean runBlockChecks(Player player, MovingData data) {
        result.reset();

        if (!data.wasOnGround()) {
            hoverCheck(player, data);
        }

        // Ground and distances.
        double vertical = data.getVerticalSpeed();

        Location ground = data.getGroundLocation();

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
                result.set(checkViolation(player, "Ascending too fast while on a ladder. vertical: " + vertical + " max: " +
                        maxAscendSpeed));
                handleCheckCancel(player, ground);
            }

            // patch for instant ladder
            if (airTicks >= 20 && vertical > maxAscendSpeed + 0.12) {
                result.set(checkViolation(player, "Ascending too fast while on a ladder. AIR: " + airTicks + " vertical: " + vertical + " " +
                        "allowed: " + maxAscendSpeed + 0.12));
                handleCheckCancel(player, ground);
            }

        }

        // descending check.
        if (isDescending && isClimbing && actuallyInAir) {
            // too fast, flag.
            if (vertical > maxDescendSpeed) {
                result.set(checkViolation(player, "Descending too fast while on a ladder. vertical: " + vertical + " allowed: " +
                        maxDescendSpeed));
                handleCheckCancel(player, ground);
            }
        }

        return result.failed();
    }


    public boolean check(Player player, MovingData data) {
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

        int ascendingMoves = data.getAscendingMoves();

        if (onGround) {
            // reset data
            data.setAscendingMoves(0);
            data.setDescendingMoves(0);
        }

        if (isAscending) {
            data.setDescendingMoves(0);
            ascendingMoves += 1;
            data.setAscendingMoves(ascendingMoves);
        }

        if (isDescending) {
            data.setAscendingMoves(0);
        }


        boolean hasActualVelocity = !isClimbing && !inLiquid && player.getVehicle() == null &&
                !velocityModifier;

        // Make sure we're not jumping too high or for too long.
        if (hasActualVelocity && isAscending) {
            if (ascendingMoves > maxAscendTime) {
                // too long, flag.
                result.set(checkViolation(player, "Ascending for too long. moves=" + ascendingMoves + " max=" + maxAscendTime));
                handleCheckCancel(player, ground);
            }

            if (vertical > getMaxJump(player)) {
                result.set(checkViolation(player, "Ascending too fast. vertical=" + vertical));
                handleCheckCancel(player, ground);
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
                result.set(checkViolation(player, "Velocity not changing while descending."));
                handleCheckCancel(player, ground);
            }

            // calculate expected falling speed.
            double expected = Math.abs((Math.pow(0.98, data.getAirTicks()) - 1) * 3.92);
            double difference = Math.abs(expected - vertical);
            // make sure we've been gliding.
            double distFromGround = LocationHelper.distanceVertical(ground, to);
            if (distFromGround > 1.6 && difference > 0.01) {
                result.set(checkViolation(player, "Velocity not expected."));
                handleCheckCancel(player, ground);
            }

        }

        // make sure the player isn't clipping through blocks
        if (vertical > 0.99) {
            int minY = Math.min(from.getBlockY(), to.getBlockY());
            int maxY = Math.max(from.getBlockY(), to.getBlockY());

            // ray trace blocks and check if there are any solid blocks between where we moved.
            for (int y = minY; y < maxY; y++) {
                // get the block.
                Block current = to.getWorld().getBlockAt(to.getBlockX(), y, to.getBlockZ());
                if (current.getType().isSolid()) {
                    // its solid, cancel.
                    boolean cancel = checkViolation(player, "Attempted to clip vertically. Block: " + current.getType()
                            + " fromY: " + from.getBlockY() + " toY: " + to.getBlockY());
                    result.set(cancel);
                    if (cancel) {
                        handleCheckCancel(player, from);
                    }
                }
            }
        }

        // TODO: Add another descend check which will complete the flight check.
        // I'm still working on it and its taking awhile.
        return result.failed();
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
