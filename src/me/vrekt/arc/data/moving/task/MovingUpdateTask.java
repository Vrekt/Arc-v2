package me.vrekt.arc.data.moving.task;

import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.moving.Flight;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.listener.ACheckListener;
import me.vrekt.arc.utilties.LocationHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MovingUpdateTask extends BukkitRunnable implements ACheckListener {
    private final Flight FLIGHT = (Flight) CHECK_MANAGER.getCheck(CheckType.FLIGHT);

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
                data.setAirTicks(airTicks >= 100 ? 100 : airTicks + 10);

                long time = System.currentTimeMillis() - data.getLastMovingUpdate();
                if (time >= 500 && data.getAirTicks() >= 40) {
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

                    // Haven't moved yet, lets check, we shouldn't need the compat flight here.
                    FLIGHT.hoverCheck(player, data);
                }
            }

        }

    }

}
