package me.vrekt.arc.violation;

import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ViolationHandler {

    private final Map<Player, ViolationData> VIOLATION_DATA = new HashMap<>();
    private final List<Player> DEBUG_LISTENERS = new ArrayList<>();

    /**
     * @param player the player
     * @return the players ViolationData.
     */
    public ViolationData getViolationData(Player player) {
        VIOLATION_DATA.putIfAbsent(player, new ViolationData());
        return VIOLATION_DATA.get(player);
    }

    /**
     * Removes the players violation data.
     *
     * @param player the player.
     */
    public void removeViolationData(Player player) {
        if (VIOLATION_DATA.containsKey(player)) {
            VIOLATION_DATA.get(player).clearData();
            VIOLATION_DATA.remove(player);
        }
    }

    /**
     * Handle a violation.
     *
     * @param player the player
     * @param check  the check
     * @return whether or not to cancel.
     */
    public boolean handleViolation(Player player, CheckType check, String information) {
        ViolationData data = getViolationData(player);
        int violationLevel = data.getViolationLevel(check) + 1;

        // increment the violation level.
        data.incrementViolationLevel(check);

        // get our levels for cancelling, banning and notifying.
        int cancel = Arc.getCheckManager().getCancelViolations(check);
        int ban = Arc.getCheckManager().getBanViolations(check);
        int notify = Arc.getCheckManager().getNotifyViolations(check);

        // check if we can ban and cancel.
        boolean bannable = Arc.getCheckManager().isCheckBannable(check);
        boolean cancellable = Arc.getCheckManager().isCheckCancellable(check);

        // notify all players with the permission arc.notify.
        if (notify != 0 && violationLevel % notify == 0) {
            // filter a list of online players with the permission.
            Collection<Player> players =
                    Bukkit.getOnlinePlayers().stream().filter(notifier -> notifier.hasPermission("arc.notify")).collect(Collectors
                            .toList());

            for (Player notifier : players) {
                // if we are in the listener list, send the violation with the attached info.
                if (DEBUG_LISTENERS.contains(notifier)) {
                    notifier.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Arc" + ChatColor.DARK_GRAY + "] "
                            + ChatColor.BLUE + player.getName() + ChatColor.WHITE + " has violated check " + ChatColor.RED
                            + check.getCheckName() + ChatColor.DARK_GRAY + " (" + ChatColor.RED + violationLevel + ChatColor.DARK_GRAY
                            + ")" + ChatColor.GRAY + " [" + information + "]");
                } else {
                    // we're not in the list, send normal violation.
                    notifier.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Arc" + ChatColor.DARK_GRAY + "] "
                            + ChatColor.BLUE + player.getName() + ChatColor.WHITE + " has violated check " + ChatColor.RED
                            + check.getCheckName() + ChatColor.DARK_GRAY + " (" + ChatColor.RED + violationLevel + ChatColor.DARK_GRAY
                            + ")");
                }
            }
        }

        if (violationLevel >= ban && bannable) {
            Arc.getArcPlayerManager().scheduleBan(player);
        }

        return violationLevel >= cancel && cancellable;
    }

    public void addOrRemoveListener(Player player) {
        if (DEBUG_LISTENERS.contains(player)) {
            removeListener(player);
            return;
        }

        addListener(player);

    }

    private void addListener(Player player) {
        DEBUG_LISTENERS.add(player);
        player.sendMessage(ChatColor.GREEN + "You will now receive debug info.");
    }

    private void removeListener(Player player) {
        DEBUG_LISTENERS.remove(player);
        player.sendMessage(ChatColor.GREEN + "You will not longer receive debug info.");
    }

    public void clearPlayerData(Player player) {
        if (DEBUG_LISTENERS.contains(player)) {
            DEBUG_LISTENERS.remove(player);
        }
    }

}
