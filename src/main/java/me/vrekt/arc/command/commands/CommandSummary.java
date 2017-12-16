package me.vrekt.arc.command.commands;

import me.vrekt.arc.Arc;
import me.vrekt.arc.chat.ChatUtility;
import me.vrekt.arc.check.CheatProbability;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.command.Command;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class CommandSummary extends Command {

    public CommandSummary() {
        super("summary");
    }

    @Override
    public void execute(Player player, String[] args) {

        if (args.length == 0) {
            player.sendMessage(usage());
            return;
        }

        Player user = Bukkit.getPlayer(args[1]);
        if (user == null) {
            player.sendMessage(ChatColor.RED + "Player not found.");
            return;
        }


        player.sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
        player.sendMessage(ChatColor.RED + ChatUtility.getCenteredMessage("Viewing report for " + ChatColor.GREEN + user.getName()));
        player.sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "----------------------------------------------------");

        // get basic info about the player.
        String OP = user.isOp() ? ChatColor.RED + "true" : ChatColor.GREEN + "false";
        String gamemode = ChatColor.GOLD + "GameMode: " + ChatColor.BLUE + StringUtils.capitalize(user.getGameMode().toString()
                .toLowerCase());
        String world = ChatColor.GOLD + "World: " + ChatColor.BLUE + user.getWorld().getName();

        // send them their total violation level
        int totalLevel = Arc.getViolationHandler().getViolationData(user).getTotalLevel();
        player.sendMessage(ChatUtility.getCenteredMessage(ChatColor.GREEN + "" + user.getName() + "'s total violation level: " + ChatColor.RED + totalLevel));

        // append the cheatprobability.
        CheatProbability probability = totalLevel <= 30 ? CheatProbability.NOT_LIKELY : totalLevel <= 60 ? CheatProbability.LIKELY :
                CheatProbability.DEFINITELY;
        player.sendMessage(ChatUtility.getCenteredMessage(ChatColor.GREEN + "Probability this player is cheating: " + ChatColor.RED +
                probability.getName()));

        // Send some basic stats.
        player.sendMessage(ChatUtility.getCenteredMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD
                + "Operator: " + OP + ChatColor.DARK_GRAY + "] [" + gamemode + ChatColor.DARK_GRAY + "] [" + world + ChatColor.DARK_GRAY +
                "]"));

        // start displaying check info.
        player.sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "----------------------------------------------------");

        Map<CheckType, Integer> checks = Arc.getViolationHandler().getViolationData(user).getViolatedChecks();
        List<String> sorted = new ArrayList<>();

        // get all checks we have failed and put them in a list to be sorted.
        for (CheckType check : checks.keySet()) {
            int violationLevel = checks.get(check);
            String centered = ChatUtility.getCenteredMessage(ChatColor.RED + check.getCheckName() + ChatColor.GOLD + " violations: " +
                    ChatColor.RED + violationLevel + ChatColor.GOLD + ".");
            sorted.add(centered);
        }

        // sort and send.
        sorted.sort(Comparator.comparingInt(String::length));
        sorted.forEach(player::sendMessage);

    }

    @Override
    public String usage() {
        return ChatColor.RED + "The correct usage is: /summary <player>";
    }
}
