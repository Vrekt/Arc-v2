package me.vrekt.arc.listener.moving;

import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckResult;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.moving.Flight;
import me.vrekt.arc.check.moving.NoFall;
import me.vrekt.arc.check.moving.Speed;
import me.vrekt.arc.check.moving.compatibility.Flight17;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.listener.ACheckListener;
import me.vrekt.arc.utilties.LocationHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MovingListener implements Listener, ACheckListener {

    private final Flight FLIGHT = (Flight) CHECK_MANAGER.getCheck(CheckType.FLIGHT);
    private final Flight17 FLIGHT_17 = (Flight17) CHECK_MANAGER.getCheck(CheckType.FLIGHT_17);

    private final NoFall NO_FALL = (NoFall) CHECK_MANAGER.getCheck(CheckType.NOFALL);
    private final Speed SPEED = (Speed) CHECK_MANAGER.getCheck(CheckType.SPEED);

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent event) {
        boolean compatibility = Arc.COMPATIBILITY;

        Player player = event.getPlayer();
        CheckResult result = new CheckResult();
        MovingData data = MovingData.getData(player);

        Location from = event.getFrom();
        Location to = event.getTo();

        data.setPreviousLocation(from);
        data.setCurrentLocation(to);

        // check if we have swapped worlds, if so stop here.
        if (from.getWorld() != to.getWorld()) {
            return;
        }

        // check if we have moved.
        boolean hasMoved = from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ();

        // check if we have moved but only from block to another block.
        boolean hasMovedByBlock = from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY()
                || from.getBlockZ() != to.getBlockZ();

        boolean canCheckFlight = CHECK_MANAGER.canCheckPlayer(player, CheckType.FLIGHT);

        if (hasMoved) {
            result.reset();

            // update our distances.
            double vertical = Math.abs(LocationHelper.distanceVertical(from, to));
            data.setLastVerticalSpeed(data.getVerticalSpeed());
            data.setVerticalSpeed(vertical);

            // recheck if we are on ground and set it.
            boolean prevGround = data.isOnGround();
            boolean onGround = LocationHelper.isOnGround(to, vertical);
            data.setOnGround(onGround);
            if (onGround) {
                data.setGroundLocation(to);
            }

            // update wasOnGround.
            boolean accAirTime = data.getAirTicks() < 20 && prevGround;
            data.setWasOnGround(accAirTime);


            // update ascending/descending.
            boolean ascending = to.getY() > from.getY() && vertical > 0.0;
            boolean descending = from.getY() > to.getY() && vertical > 0.0;
            data.setAscending(ascending);
            data.setDescending(descending);

            // check if we are climbing or not.
            boolean isClimbing = (from.getBlock().getType() == Material.LADDER || from.getBlock().getType() == Material.VINE
                    || to.getBlock().getType() == Material.LADDER || to.getBlock().getType() == Material.VINE);
            data.setClimbing(isClimbing);

            double velocity = player.getVelocity().length();
            data.getVelocityData().setLastVelocity(data.getVelocityData().getCurrentVelocity());
            data.getVelocityData().setCurrentVelocity(velocity);

            if (canCheckFlight) {
                // get the result and cancel if there is an alternate setback.
                CheckResult flightResult = FLIGHT.check(player, data);
                result.set(flightResult.failed());
                if (result.failed()) {
                    if (flightResult.getCancelLocation() != null) {
                        event.setTo(flightResult.getCancelLocation());
                    } else {
                        event.setTo(data.getGroundLocation());
                    }
                }
            }

        }


        if (hasMovedByBlock) {
            result.reset();
            if (canCheckFlight) {
                // check based on compatibility
                CheckResult flightResult;
                if (compatibility) {
                    flightResult = FLIGHT_17.runBlockChecks(player, data);
                } else {
                    flightResult = FLIGHT.runBlockChecks(player, data);
                }

                // cancel if there is an alternate setback.
                result.set(flightResult.failed());
                if (result.failed()) {
                    if (flightResult.getCancelLocation() != null) {
                        event.setTo(flightResult.getCancelLocation());
                    } else {
                        event.setTo(data.getGroundLocation());
                    }
                }

            }

            boolean canCheckNoFall = CHECK_MANAGER.canCheckPlayer(player, CheckType.NOFALL);
            if (canCheckNoFall) {
                NO_FALL.check(player, data);
            }

            boolean failed = SPEED.check(player, data);
            if (failed) {
                event.setTo(data.getSetback());
            }
        }

    }

}
