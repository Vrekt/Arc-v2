package me.vrekt.arc.data.inventory;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class InventoryData {
    private static final Map<Player, InventoryData> DATA_MAP = new HashMap<>();

    /**
     * Retrieve the players data.
     *
     * @param player the player
     * @return the data
     */
    public static InventoryData getData(Player player) {
        DATA_MAP.putIfAbsent(player, new InventoryData());
        return DATA_MAP.get(player);
    }

    /**
     * Remove the players data.
     *
     * @param player the player
     */
    public static void removeData(Player player) {
        if (DATA_MAP.containsKey(player)) {
            DATA_MAP.remove(player);
        }
    }

    private long consumeTime;
    private long lastConsumeSwitch;

    public long getConsumeTime() {
        return consumeTime;
    }

    public void setConsumeTime(long consumeTime) {
        this.consumeTime = consumeTime;
    }

    public long getLastItemSwitch() {
        return lastConsumeSwitch;
    }

    public void setLastItemSwitch(long lastConsumeSwitch) {
        this.lastConsumeSwitch = lastConsumeSwitch;
    }
}
