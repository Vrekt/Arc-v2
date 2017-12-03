package me.vrekt.arc;

import com.comphenix.protocol.ProtocolLibrary;
import me.vrekt.arc.check.management.CheckManager;
import me.vrekt.arc.data.moving.task.MovingUpdateTask;
import me.vrekt.arc.exemption.ExemptionManager;
import me.vrekt.arc.listener.PlayerListener;
import me.vrekt.arc.listener.combat.FightListener;
import me.vrekt.arc.listener.inventory.InventoryListener;
import me.vrekt.arc.listener.packet.PacketListener;
import me.vrekt.arc.violation.ViolationHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Arc extends JavaPlugin {
    private static Plugin thisPlugin;
    public static boolean COMPATIBILITY = false;

    private static CheckManager checkManager;

    private static final ExemptionManager EXEMPTION_MANAGER = new ExemptionManager();
    private static final ViolationHandler VIOLATION_HANDLER = new ViolationHandler();

    /**
     * Gets called when the plugin is enabled.
     */
    public void onEnable() {
        thisPlugin = this;

        // read/create the configuration.
        getLogger().info("Reading configuration...");
        File config = new File(getDataFolder() + "/config.yml");
        if (!config.exists()) {
            saveDefaultConfig();
        }

        checkManager = new CheckManager(getConfig());
        checkManager.initializeAllChecks();

        getLogger().info("Checking spigot version....");

        if (Bukkit.getBukkitVersion().contains("1.7")) {
            getLogger().info("Switching to 1.7 compat checks and listeners.");
            COMPATIBILITY = true;
        }

        new MovingUpdateTask().runTaskTimer(this, 0, 1);
        getServer().getPluginManager().registerEvents(new FightListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        // TODO: New PacketListener might be needed for 1.7 (not sure).
        new PacketListener().startListening(this, ProtocolLibrary.getProtocolManager());

    }

    /**
     * Handles cleaning up.
     */
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            // remove exemption and violation data.
            VIOLATION_HANDLER.removeViolationData(player);
            EXEMPTION_MANAGER.clearData(player);
        }
    }

    /**
     * @return an instance of Arc.
     */
    public static Plugin getPlugin() {
        return thisPlugin;
    }

    /**
     * @return the check manager.
     */
    public static CheckManager getCheckManager() {
        return checkManager;
    }

    /**
     * @return the exemption manager.
     */
    public static ExemptionManager getExemptionManager() {
        return EXEMPTION_MANAGER;
    }

    /**
     * @return the violation handler.
     */
    public static ViolationHandler getViolationHandler() {
        return VIOLATION_HANDLER;
    }
}
