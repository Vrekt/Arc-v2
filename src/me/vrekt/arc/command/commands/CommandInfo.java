package me.vrekt.arc.command.commands;

import me.vrekt.arc.Arc;
import me.vrekt.arc.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class CommandInfo extends Command {

    public CommandInfo() {
        super("info");
    }

    @Override
    public void execute(Player player, String[] args) {
        Logger logger = Arc.getPlugin().getLogger();
        logger.info("Arc is currently running version: " + Arc.VERSION);
        logger.info("Compatibility?: " + Arc.COMPATIBILITY);
        Arc.getCheckManager().getChecks().forEach(check -> logger.info("Check: " + check.getCheck().getCheckName() + " is registered."));
        logger.info("Current PacketListener state: " + Arc.getPacketListener().isListening());
        player.sendMessage(ChatColor.GREEN + "Information was logged to the console.");
    }

    @Override
    public String usage() {
        return "/arc info";
    }
}
