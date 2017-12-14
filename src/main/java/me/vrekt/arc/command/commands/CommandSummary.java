package me.vrekt.arc.command.commands;

import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheatProbability;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.command.Command;
import me.vrekt.arc.violation.ViolationData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

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

        // create inventory and an empty pane.
        Inventory inventory = Bukkit.createInventory(null, 27, "Summary for " + user.getName());
        ItemStack itemPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);

        ItemMeta metaPane = itemPane.getItemMeta();
        metaPane.setDisplayName(ChatColor.RED + "");
        itemPane.setItemMeta(metaPane);

        ItemStack itemPaper = new ItemStack(Material.PAPER);
        int index = 0;

        for (Check check : Arc.getCheckManager().getChecks()) {
            CheckType type = check.getCheck();

            ItemMeta metaPaper = itemPaper.getItemMeta();
            metaPaper.setDisplayName(ChatColor.RED + "Check: " + ChatColor.GOLD + type.getCheckName());

            // get violationData and add the lore.
            ViolationData data = Arc.getViolationHandler().getViolationData(user);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.WHITE + "Violations: " + ChatColor.RED
                    + data.getViolationLevel(type));

            metaPaper.setLore(lore);
            itemPaper.setItemMeta(metaPaper);

            // add the item
            inventory.setItem(index++, itemPaper);
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, itemPane);
            }
        }

        ItemStack itemApple = new ItemStack(Material.APPLE);
        ItemStack itemSword = new ItemStack(Material.IRON_SWORD);
        ItemStack itemBook = new ItemStack(Material.BOOK);

        String OP = user.isOp() ? ChatColor.RED + "true" : ChatColor.GREEN + "false";

        ItemMeta metaBook = itemBook.getItemMeta();
        metaBook.setDisplayName(ChatColor.GOLD + "Player information: ");

        ItemMeta metaSword = itemSword.getItemMeta();
        metaSword.setDisplayName(ChatColor.RED + "Ban this player.");

        // set their cheat probability based on the total VL.
        int totalLevel = Arc.getViolationHandler().getViolationData(user).getTotalLevel();
        CheatProbability probability = totalLevel <= 20 ? CheatProbability.NOT_LIKELY : totalLevel >= 30 ? CheatProbability.LIKELY :
                totalLevel >= 50 ?
                        CheatProbability.DEFINITELY : CheatProbability.NOT_LIKELY;

        ItemMeta metaApple = itemApple.getItemMeta();
        metaApple.setDisplayName(ChatColor.RED + "Probability this player is cheating: " + ChatColor.AQUA + probability.getName());

        // add their info to a list and set the lore.
        List<String> info = new ArrayList<>();
        info.add(ChatColor.GOLD + "Operator: " + OP);
        info.add(ChatColor.GOLD + "GameMode: " + ChatColor.BLUE + user.getGameMode().toString());
        info.add(ChatColor.GOLD + "World: " + ChatColor.BLUE + user.getWorld().getName());

        // set item flags and lore.
        metaBook.setLore(info);
        itemBook.setItemMeta(metaBook);

        itemSword.setItemMeta(metaSword);
        itemApple.setItemMeta(metaApple);

        inventory.setItem(21, itemApple);
        inventory.setItem(22, itemSword);
        inventory.setItem(23, itemBook);

        player.openInventory(inventory);
    }

    @Override
    public String usage() {
        return ChatColor.RED + "The correct usage is: /summary <player>";
    }
}
