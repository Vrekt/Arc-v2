package me.vrekt.arc.utilties;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class FightHelper {

    /**
     * Check if an attack was a critical.
     *
     * @param player
     * @return
     */
    public static boolean isCritical(Player player) {

        boolean onGround = ((Entity) player).isOnGround();
        return !onGround && player.getFallDistance() > 0.0F && !LocationHelper.isInLiquid(player.getLocation())
                && (player.getLocation().getBlock().getType() != Material.LADDER
                || player.getLocation().getBlock().getType() != Material.VINE)
                && player.getVehicle() == null && !player.hasPotionEffect(PotionEffectType.BLINDNESS);
    }

}
