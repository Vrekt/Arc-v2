package me.vrekt.arc.utilties;

import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class ConnectionUtility {

    /**
     * @param player the player
     * @return their ping.
     */
    public static int getPlayerPing(Player player) {
        try {
            Object serverPlayer = player.getClass().getMethod("getHandle").invoke(player);
            return (int) serverPlayer.getClass().getField("ping").get(serverPlayer);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
