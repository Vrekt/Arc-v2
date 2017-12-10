package me.vrekt.arc.command;


import me.vrekt.arc.Arc;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandBase implements org.bukkit.command.CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command thisCommand, String alias, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You cannot execute this command as console.");
            return true;
        }

        Player player = (Player) commandSender;
        if (strings.length >= 1) {
            String command = strings[0];
            boolean isCommand = Arc.getCommandExecutor().executeCommand(player, command, strings);
            if (!isCommand) {
                getHelp(player);
                return true;
            }
        } else {
            getHelp(player);
            return true;
        }

        return true;
    }

    /**
     * Display help information.
     *
     * @param player the player.
     */
    private void getHelp(Player player) {

        player.sendMessage(ChatColor.RED + "Arc AntiCheat. [" + Arc.VERSION + "]");
        player.sendMessage(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + "-------------------------------------");
        player.sendMessage(ChatColor.GRAY + "/arc cancelban <player> \n" + ChatColor.GOLD
                + "Cancels a ban scheduled by Arc. \n");
        player.sendMessage("");
        player.sendMessage(
                ChatColor.GRAY + "/arc info \n" + ChatColor.GOLD + "Log Arc debug information to the console.");
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "/arc debug \n" + ChatColor.GOLD
                + "Toggle debug information to be attached to the violation.");
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "/arc gui \n" + ChatColor.GOLD + "Allows you to manage Arc from a GUI.");
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "/arc summary <player> \n" + ChatColor.GOLD
                + "Allows you to view information about a player.");

        player.sendMessage(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + "-------------------------------------");

    }

}
