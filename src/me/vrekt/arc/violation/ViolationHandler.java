package me.vrekt.arc.violation;

import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ViolationHandler {

    private final Map<Player, ViolationData> VIOLATION_DATA = new HashMap<>();

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
     * @param player      the player
     * @param check       the check
     * @param information violation information/debug.
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
            Bukkit.broadcast(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Arc" + ChatColor.DARK_GRAY + "] "
                    + ChatColor.BLUE + player.getName() + ChatColor.WHITE + " has violated check " + ChatColor.RED
                    + check.getCheckName() + ChatColor.DARK_GRAY + " (" + ChatColor.RED + violationLevel + ChatColor.DARK_GRAY
                    + ") " + ChatColor.GRAY + "[" + information + "]", "arc.notify");
        }

        if (violationLevel >= ban && bannable) {
            // TODO: Schedule ban.
        }

        return violationLevel >= cancel && cancellable;
    }

}
