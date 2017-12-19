package me.vrekt.arc.listener.inventory;

import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.inventory.FastConsume;
import me.vrekt.arc.data.inventory.InventoryData;
import me.vrekt.arc.listener.ACheckListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener, ACheckListener {
    private final FastConsume FAST_CONSUME = (FastConsume) CHECK_MANAGER.getCheck(CheckType.FASTCONSUME);

    @EventHandler(priority = EventPriority.LOWEST)
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        InventoryData data = InventoryData.getData(player);

        boolean canCheckFastConsume = CHECK_MANAGER.canCheckPlayer(player, CheckType.FASTCONSUME);
        if (canCheckFastConsume) {
            // check and cancel if we failed.
            boolean cancel = FAST_CONSUME.check(player, data);
            event.setCancelled(cancel);
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            InventoryData data = InventoryData.getData(player);
            // check if we are consuming an item.
            if (item.getType().isEdible() || item.getType() == Material.POTION) {
                // update data.
                data.setConsumeTime(System.currentTimeMillis());
            }
        }

    }

    /**
     * Handle the GUI stuff.
     */
    @EventHandler
    public void onClick(InventoryClickEvent event) {

        Inventory inventory = event.getInventory();
        if (inventory.getTitle().contains("Summary for ")) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();
            Player viewer = (Player) event.getWhoClicked();
            if (item.getType() == Material.IRON_SWORD) {
                Player player = Bukkit.getPlayer(inventory.getTitle().split(" ")[2]);
                if (player == null) {
                    viewer.sendMessage(ChatColor.RED + "This player has logged out.");
                    viewer.closeInventory();
                    return;
                }

                Arc.getArcPlayerManager().scheduleBan(player);
            }

        }

    }

}
