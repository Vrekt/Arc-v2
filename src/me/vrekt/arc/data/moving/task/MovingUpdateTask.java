package me.vrekt.arc.data.moving.task;

import me.vrekt.arc.data.moving.MovingData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MovingUpdateTask extends BukkitRunnable {

    @Override
    public void run() {
        // loop through all players and update air data.
        for (Player player : Bukkit.getOnlinePlayers()) {
            MovingData data = MovingData.getData(player);
            if (!data.isOnGround()) {
                data.setAirTicks(data.getAirTicks() + 1);
            }
        }
    }

}
