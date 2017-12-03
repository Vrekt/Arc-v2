package me.vrekt.arc.listener;

import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.data.combat.FightData;
import me.vrekt.arc.data.inventory.InventoryData;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.utilties.LocationHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    /**
     * Remove the player from all maps and lists.
     *
     * @param event the event.
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Arc.getExemptionManager().clearData(player);
        Arc.getViolationHandler().removeViolationData(player);

        // clear check data.
        MovingData.removeData(player);
        FightData.removeData(player);
        InventoryData.removeData(player);

    }

    /**
     * Handle players joining.
     *
     * @param event the event.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        MovingData data = MovingData.getData(player);

        // exempt on join for morepackets.
        Arc.getExemptionManager().addExemption(player, CheckType.MOREPACKETS, 20);

        data.setOnGround(LocationHelper.isOnGround(player.getLocation(), 0));
        data.setCurrentLocation(player.getLocation());
        if (data.isOnGround()) {
            data.setGroundLocation(player.getLocation());
        }

    }

}
