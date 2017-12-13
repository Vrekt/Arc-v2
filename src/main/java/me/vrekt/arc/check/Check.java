package me.vrekt.arc.check;

import me.vrekt.arc.Arc;
import org.bukkit.entity.Player;

public abstract class Check {

    private CheckType check;
    protected CheckResult result = new CheckResult();

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
     * @param player the player
     * @return to cancel or not.
     */
    protected boolean checkViolation(Player player, String information) {
        return Arc.getViolationHandler().handleViolation(player, check, information);
    }

}
