package me.vrekt.arc.exemption;

import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.exemption.data.ExemptionData;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class ExemptionManager {

    private final CheckType[] EXEMPT_BECAUSE_FLYING = new CheckType[]{CheckType.CRITICALS, CheckType.SPEED, CheckType.NOFALL,
            CheckType
                    .FLIGHT};

    private final Map<Player, ExemptionData> EXEMPTION_DATA = new HashMap<>();
    private final Map<Player, CheckType> PERMANENT_EXEMPTIONS = new HashMap<>();

    /**
     * Check if the player is exempt.
     *
     * @param check  the check
     * @param player the check
     * @return if we are exempted or not.
     */
    public boolean isPlayerExempt(Player player, CheckType check) {

        // check for common exemptions first.
        boolean hasExemption = checkCommonExemptions(player, check);
        if (hasExemption) {
            return true;
        }

        // iterate through the array of checks that need to be exempted for if we are flying.
        for (CheckType element : EXEMPT_BECAUSE_FLYING) {
            if (!(element.equals(check))) {
                continue;
            }

            if (Arc.COMPATIBILITY) {
                hasExemption = player.getGameMode() == GameMode.CREATIVE || player.getAllowFlight
                        () || player
                        .isFlying();
            } else {
                hasExemption = player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR || player
                        .getAllowFlight
                                () || player
                        .isFlying();
            }
            break;
        }

        return hasExemption;
    }

    /**
     * Checks against permission and time exemptions.
     *
     * @param player the player
     * @param check  the check
     * @return true if we are exempted, false if not.
     */
    private boolean checkCommonExemptions(Player player, CheckType check) {
        return hasTimeExemption(player, check) || hasPermission(player) || hasPermanentExemption(player, check) ||
                gameModeChangeExemption(player);
    }

    /**
     * @param player the player
     * @param check  the check
     * @return if we have a time exemption or not.
     */
    private boolean hasTimeExemption(Player player, CheckType check) {
        ExemptionData data = EXEMPTION_DATA.getOrDefault(player, null);
        return data != null && data.isCheckExempted(check);
    }

    /**
     * @param player the player
     * @return if we are exempt because of a recent GameMode change.
     */
    private boolean gameModeChangeExemption(Player player) {
        MovingData movingData = MovingData.getData(player);
        if (movingData.getLastGameModeChange() == 0) {
            return false;
        }
        return (System.currentTimeMillis() - movingData.getLastGameModeChange()) <= 1500;
    }

    /**
     * @param player the player
     * @return if we have the bypass permission or not.
     */
    private boolean hasPermission(Player player) {
        return player.hasPermission("arc.bypass");
    }

    /**
     * @param player the player
     * @param check  the check
     * @return if we are permanently exempted.
     */
    private boolean hasPermanentExemption(Player player, CheckType check) {
        return PERMANENT_EXEMPTIONS.containsKey(player) && PERMANENT_EXEMPTIONS.get(player).equals(check);
    }

    /**
     * Get the players exemption data.
     *
     * @param player the player.
     * @return the exemption data.
     */
    private ExemptionData getData(Player player) {
        return EXEMPTION_DATA.getOrDefault(player, null);
    }

    /**
     * Add an exemption.
     *
     * @param check  the check
     * @param player the player
     * @param ticks  how long
     */
    public void addExemption(Player player, CheckType check, long ticks) {
        // make sure we aren't already exempt!
        if (isPlayerExempt(player, check)) {
            return;
        }

        // put the exemption in.
        ExemptionData data = getData(player);
        if (data == null) {
            data = new ExemptionData(check, ticks);
            EXEMPTION_DATA.put(player, data);
        } else {
            data.exemptCheck(check, ticks);
            EXEMPTION_DATA.put(player, data);
        }

        // start a task to keep track of the exempted player.
        new BukkitRunnable() {

            @Override
            public void run() {
                // player doesn't exist anymore or isn't online, cancel.
                if (player == null || !player.isOnline()) {
                    this.cancel();
                    return;
                }

                // remove the player.
                getData(player).removeExemption(check);

            }
        }.runTaskLater(Arc.getPlugin(), ticks);

    }

    /**
     * Clear exemption data.
     *
     * @param player the player
     */
    public void clearData(Player player) {
        if (EXEMPTION_DATA.containsKey(player)) {
            EXEMPTION_DATA.remove(player);
        }

        if (PERMANENT_EXEMPTIONS.containsKey(player)) {
            PERMANENT_EXEMPTIONS.remove(player);
        }

    }

}
