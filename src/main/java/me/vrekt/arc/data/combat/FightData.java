package me.vrekt.arc.data.combat;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class FightData {
    private static final Map<Player, FightData> DATA_MAP = new HashMap<>();
    private long lastHealthEvent;

    /**
     * Retrieve the players data.
     *
     * @param player the player
     * @return the data
     */
    public static FightData getData(Player player) {
        DATA_MAP.putIfAbsent(player, new FightData());
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

    public long getLastHealthEvent() {
        return lastHealthEvent;
    }

    public void setLastHealthEvent(long lastHealthEvent) {
        this.lastHealthEvent = lastHealthEvent;
    }
}
