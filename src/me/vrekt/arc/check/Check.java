package me.vrekt.arc.check;

import me.vrekt.arc.Arc;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public abstract class Check {

    private CheckType check;

    /**
     * @param check the check.
     */
    public Check(CheckType check) {
        this.check = check;
    }

    /**
     * @return the check.
     */
    public CheckType getCheck() {
        return check;
    }

    /**
     * Handle the violation.
     *
     * @param player      the player
     * @param information the information.
     * @return to cancel or not.
     */
    public boolean checkViolation(Player player, String information) {
        return Arc.getViolationHandler().handleViolation(player, check, information);
    }

    /**
     * Handle teleporting the player to a setback.
     *
     * @param player   the player
     * @param location the location.
     */
    public void handleCheckCancel(Player player, Location location) {
        if (location == null) {
            return;
        }
        player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

}
