package me.vrekt.arc.listener.compatibility;

import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.moving.NoFall;
import me.vrekt.arc.check.moving.Speed;
import me.vrekt.arc.check.moving.compatibility.Flight17;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.listener.ACheckListener;
import me.vrekt.arc.utilties.LocationHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Moving17 extends BukkitRunnable implements Listener, ACheckListener {

    /**
     * Currently the 1.7 listener (this) could just use the normal MovingListener but later it will be helpful
     * to have this class since 1.7 doesnt need data/checks for ice/slimeblocks, etc.
     */

    private final Flight17 FLIGHT = (Flight17) CHECK_MANAGER.getCheck(CheckType.FLIGHT);
    private final NoFall NO_FALL = (NoFall) CHECK_MANAGER.getCheck(CheckType.NOFALL);

    private final Speed SPEED = (Speed) CHECK_MANAGER.getCheck(CheckType.SPEED);

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        MovingData data = MovingData.getData(player);

        // update our location data.
        Location from = event.getFrom();
        Location to = event.getTo();
        data.setCurrentLocation(to);
        data.setPreviousLocation(from);

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
            // update our last move time.
            data.setLastMovingUpdate(System.currentTimeMillis());

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
                FLIGHT.check(player, data);
            }

        }

        if (hasMovedByBlock) {
            if (canCheckFlight) {
                FLIGHT.runBlockChecks(player, data);
            }

            boolean canCheckNoFall = CHECK_MANAGER.canCheckPlayer(player, CheckType.NOFALL);
            if (canCheckNoFall) {
                NO_FALL.check(player, data);
            }

            SPEED.check(player, data);

        }

    }

    @Override
    public void run() {
        // loop through all players and update moving data.
        for (Player player : Bukkit.getOnlinePlayers()) {
            MovingData data = MovingData.getData(player);
            if (data.getLastMovingUpdate() == 0) {
                data.setLastMovingUpdate(System.currentTimeMillis());
                continue;
            }
            boolean canCheckFlight = CHECK_MANAGER.canCheckPlayer(player, CheckType.FLIGHT);
            if (!canCheckFlight) {
                continue;
            }

            // update data.
            if (!data.isOnGround()) {
                // update air time.
                int airTicks = data.getAirTicks();
                data.setAirTicks(airTicks + 10);

                long time = System.currentTimeMillis() - data.getLastMovingUpdate();
                if (time >= 500) {
                    data.setLastMovingUpdate(System.currentTimeMillis());
                    Location from = data.getCurrentLocation();
                    // make sure we have a "previous" location.
                    if (from == null) {
                        continue;
                    }

                    Location to = player.getLocation();
                    // calculate and update vertical.
                    double vertical = Math.abs(LocationHelper.distanceVertical(from, to));
                    // recalculate onground.
                    boolean onGround = LocationHelper.isOnGround(to, vertical);
                    if (onGround) {
                        data.setGroundLocation(player.getLocation());
                        data.setOnGround(true);
                        continue;
                    }

                    data.setLastVerticalSpeed(vertical == 0.0 ? vertical : data.getVerticalSpeed());
                    data.setVerticalSpeed(vertical);

                    // update ascending/descending.
                    boolean ascending = to.getY() > from.getY() && vertical > 0.0;
                    boolean descending = from.getY() > to.getY() && vertical > 0.0;
                    data.setAscending(ascending);
                    data.setDescending(descending);

                    // Haven't moved yet, lets check.
                    FLIGHT.hoverCheck(player, data);

                }
            }

        }

    }
}
