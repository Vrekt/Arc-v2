package me.vrekt.arc.command.commands;

import me.vrekt.arc.Arc;
import me.vrekt.arc.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandCancelBan extends Command {

    public CommandCancelBan() {
        super("cancelban");
    }

    @Override
    public void execute(Player player, String[] args) {

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Incorrect usage: " + usage());
            return;
        }

        Player ban = Bukkit.getPlayer(args[1]);
        if (ban == null) {
            player.sendMessage(ChatColor.RED + "Player not found.");
            return;
        }

        if (Arc.getArcPlayerManager().isScheduledForBan(ban.getUniqueId())) {
            Arc.getArcPlayerManager().cancelBan(ban.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "The scheduled ban for " + ChatColor.RED + ban.getName() + ChatColor.GREEN + " has been " +
                    "cancelled.");
            return;
        }

        player.sendMessage(ChatColor.RED + "This player is not scheduled to be banned.");
    }

    @Override
    public String usage() {
        return "/arc cancelban <name>";
    }
}
