package me.vrekt.arc.management;

import me.vrekt.arc.Arc;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ArcPlayerManager {

    private final List<UUID> BAN_QUEUE = new ArrayList<>();

    /**
     * Schedule a ban.
     *
     * @param player the player.
     */
    public void scheduleBan(Player player) {

        UUID uuid = player.getUniqueId();
        String name = player.getName();

        if (BAN_QUEUE.contains(uuid)) {
            return;
        }

        // add and broadcast.
        BAN_QUEUE.add(uuid);
        int time = Arc.getArcConfiguration().getBanTime();

        Bukkit.broadcast(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Arc" + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE
                + player.getName() + ChatColor.WHITE + " is scheduled to be banned in " + ChatColor.RED + time
                + ChatColor.WHITE + " seconds.", "arc.notify");

        long timeScheduled = System.currentTimeMillis();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!BAN_QUEUE.contains(uuid)) {
                    this.cancel();
                }
                if (System.currentTimeMillis() - timeScheduled >= time * 1000) {
                    if (BAN_QUEUE.contains(uuid)) {
                        // get the ban data and ban the player.
                        String reason = ChatColor.RED + "You have been banned for cheating.";

                        Date date = Arc.getArcConfiguration().getBanDate();
                        BanList.Type type = Arc.getArcConfiguration().getBanType();
                        Bukkit.getBanList(type).addBan(name, reason, date, null);

                        if (Arc.getArcConfiguration().shouldBroadcastBan()) {
                            String message = Arc.getArcConfiguration().getBroadcastMessage().replace("%player%", player.getName());
                            Bukkit.broadcastMessage(message);
                        }

                        if (player.isOnline()) {
                            player.kickPlayer(reason);
                        }

                        // remove the player.
                        BAN_QUEUE.remove(uuid);
                    }
                }
            }
        }.runTaskTimer(Arc.getPlugin(), 0, 20);

    }

    /**
     * Check if we are scheduled for a ban.
     *
     * @param uuid the player's UUID.
     * @return true, if the player is scheduled for a ban.
     */
    public boolean isScheduledForBan(UUID uuid) {
        return BAN_QUEUE.contains(uuid);
    }

    /**
     * Cancel the ban.
     *
     * @param uuid the player's UUID.
     */
    public void cancelBan(UUID uuid) {
        BAN_QUEUE.remove(uuid);
    }

}
