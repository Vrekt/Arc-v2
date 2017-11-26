package me.vrekt.arc.check.management;

import me.vrekt.arc.Arc;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.moving.Flight;
import me.vrekt.arc.check.moving.MorePackets;
import me.vrekt.arc.check.moving.NoFall;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckManager {

    private final Map<CheckType, CheckData> CHECK_DATA = new HashMap<>();
    private final List<Check> CHECKS = new ArrayList<>();

    private FileConfiguration configuration;

    /**
     * Handle getting all the check data.
     *
     * @param configuration the configuration file.
     */
    public CheckManager(FileConfiguration configuration) {
        this.configuration = configuration;
        for (CheckType check : CheckType.values()) {
            String checkName = check.getCheckName().toLowerCase();

            ConfigurationSection section = configuration.getConfigurationSection(checkName);
            boolean isEnabled = configuration.getBoolean(checkName + ".enabled");

            // if were not enabled, continue.
            if (!isEnabled) {
                continue;
            }

            // collect the violation levels from the config.
            int banViolations = section.getInt("ban");
            int cancelViolations = section.getInt("cancel-vl");
            int notifyViolations = section.getInt("notify");

            // populate the map with the new check data.
            boolean cancelCheck = section.getBoolean("cancel");
            CheckData data = new CheckData(notifyViolations, cancelViolations, banViolations, cancelCheck, banViolations > 0);
            CHECK_DATA.put(check, data);

            Arc.getPlugin().getLogger().info("Finished getting data for check: " + checkName);
        }

    }

    /**
     * Add all the checks to the map.
     */
    public void initializeAllChecks() {
        CHECKS.add(new Flight());
        CHECKS.add(new MorePackets());
        CHECKS.add(new NoFall());
    }

    /**
     * @param check the check.
     * @return if the check is enabled.
     */
    public boolean isCheckEnabled(CheckType check) {
        return CHECK_DATA.containsKey(check);
    }

    /**
     * @param check the check.
     * @return if the check is bannable at a certain VL.
     */
    public boolean isCheckBannable(CheckType check) {
        return CHECK_DATA.get(check).shouldBanCheck();
    }

    /**
     * @param check the check.
     * @return if the check is cancellable at a certain VL.
     */
    public boolean isCheckCancellable(CheckType check) {
        return CHECK_DATA.get(check).shouldCancelCheck();
    }

    /**
     * @param check the check.
     * @return ban violations for the check.
     */
    public int getBanViolations(CheckType check) {
        return CHECK_DATA.get(check).getBan();
    }

    /**
     * @param check the check.
     * @return the cancel violations for the check.
     */
    public int getCancelViolations(CheckType check) {
        return CHECK_DATA.get(check).getCancel();
    }

    /**
     * @param check the check.
     * @return the notify violations for the check.
     */
    public int getNotifyViolations(CheckType check) {
        return CHECK_DATA.get(check).getNotify();
    }

    /**
     * Checks if the player isn't exempt.
     *
     * @param check  the check
     * @param player the player
     * @return if we can check the player for this check.
     */
    public boolean canCheckPlayer(Player player, CheckType check) {
        return !Arc.getExemptionManager().isPlayerExempt(player, check);
    }

    /**
     * Get a value from the config. Used for checks.
     *
     * @param check     the check.
     * @param valueName the value name.
     * @return an int value from the config.
     */
    public int getValueInt(CheckType check, String valueName) {
        return configuration.getConfigurationSection(check.name().toLowerCase()).getInt(valueName);
    }

    /**
     * Get a value from the config.
     *
     * @param check     the check.
     * @param valueName the value name.
     * @return the double value.
     */
    public double getValueDouble(CheckType check, String valueName) {
        return configuration.getConfigurationSection(check.name().toLowerCase()).getDouble(valueName);
    }

    /**
     * Get a check.
     *
     * @param type the check
     * @return the check.
     */
    public Check getCheck(CheckType type) {
        return CHECKS.stream().filter(check -> check.getCheck() == type).findAny().orElse(null);
    }

}
