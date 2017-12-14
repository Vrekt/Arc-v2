package me.vrekt.arc;

import com.comphenix.protocol.ProtocolLibrary;
import me.vrekt.arc.check.management.CheckManager;
import me.vrekt.arc.command.CommandBase;
import me.vrekt.arc.command.CommandExecutor;
import me.vrekt.arc.config.ArcConfiguration;
import me.vrekt.arc.data.moving.task.MovingUpdateTask;
import me.vrekt.arc.exemption.ExemptionManager;
import me.vrekt.arc.listener.PlayerListener;
import me.vrekt.arc.listener.combat.FightListener;
import me.vrekt.arc.listener.inventory.InventoryListener;
import me.vrekt.arc.listener.moving.MovingListener;
import me.vrekt.arc.listener.packet.PacketListener;
import me.vrekt.arc.management.ArcPlayerManager;
import me.vrekt.arc.violation.ViolationHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Arc extends JavaPlugin {

    private static final ExemptionManager EXEMPTION_MANAGER = new ExemptionManager();
    private static final ViolationHandler VIOLATION_HANDLER = new ViolationHandler();
    private static final CommandExecutor COMMAND_EXECUTOR = new CommandExecutor();
    private static final ArcConfiguration ARC_CONFIGURATION = new ArcConfiguration();
    /**
     * ARC INFO/COMPAT
     **/
    public static boolean COMPATIBILITY = false;
    public static String VERSION = "1.0.3-b1";
    private static Plugin thisPlugin;
    private static CheckManager checkManager;
    private static PacketListener packetListener;
    private static ArcPlayerManager arcPlayerManager;

    /**
     * Gets called when the plugin is enabled.
     */
    public void onEnable() {
        thisPlugin = this;

        // read/create the configuration.
        getLogger().info("Reading configuration...");
        File config = new File(getDataFolder(), "config.yml");
        if (!config.exists()) {
            saveDefaultConfig();
        }

        getLogger().info("Checking spigot version....");
        if (Bukkit.getBukkitVersion().contains("1.7")) {
            getLogger().info("Switching to 1.7 compat checks and listeners.");
            COMPATIBILITY = true;
        }

        checkManager = new CheckManager(getConfig());
        checkManager.initializeAllChecks();
        ARC_CONFIGURATION.read(getConfig());

        arcPlayerManager = new ArcPlayerManager();

        new MovingUpdateTask().runTaskTimer(this, 0, 1);
        getServer().getPluginManager().registerEvents(new FightListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(new MovingListener(), this);

        packetListener = new PacketListener();
        packetListener.startListening(this, ProtocolLibrary.getProtocolManager());

        getCommand("arc").setExecutor(new CommandBase());
    }


    /**
     * Handles cleaning up.
     */
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            // remove exemption and violation data.
            VIOLATION_HANDLER.removeViolationData(player);
            EXEMPTION_MANAGER.clearData(player);

            // TODO: Remove other shit? I mean you really shouldn't reload anyways

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
     * @return the packet listener.
     */
    public static PacketListener getPacketListener() {
        return packetListener;
    }

    /**
     * @return the player manager.
     */
    public static ArcPlayerManager getArcPlayerManager() {
        return arcPlayerManager;
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

    /**
     * @return the command executor.
     */
    public static CommandExecutor getCommandExecutor() {
        return COMMAND_EXECUTOR;
    }

    /**
     * @return the configuration.
     */
    public static ArcConfiguration getArcConfiguration() {
        return ARC_CONFIGURATION;
    }

}
