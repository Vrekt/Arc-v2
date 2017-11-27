package me.vrekt.arc.listener.inventory;

import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.inventory.FastConsume;
import me.vrekt.arc.data.inventory.InventoryData;
import me.vrekt.arc.listener.ACheckListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener, ACheckListener {
    private final FastConsume FAST_CONSUME = (FastConsume) CHECK_MANAGER.getCheck(CheckType.FASTCONSUME);


    @EventHandler(priority = EventPriority.HIGHEST)
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

}
