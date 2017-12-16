package me.vrekt.arc.command.commands;

import me.vrekt.arc.Arc;
import me.vrekt.arc.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandReload extends Command {

    public CommandReload() {
        super("reload");
    }

    @Override
    public void execute(Player player, String[] args) {
        player.sendMessage(ChatColor.GREEN + "Reloading configuration..");

        Arc.getCheckManager().reload(Arc.getPlugin().getConfig());
        Arc.getArcConfiguration().read(Arc.getPlugin().getConfig());

        player.sendMessage(ChatColor.GREEN + "Configuration has been reloaded.");
    }

    @Override
    public String usage() {
        return "/arc reload";
    }
}
