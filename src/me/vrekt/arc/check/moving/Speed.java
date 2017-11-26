package me.vrekt.arc.check.moving;

import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.utilties.LocationHelper;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Speed extends Check {

    public Speed() {
        super(CheckType.SPEED);
    }

    public void check(MovingData data, Player player) {

        Location from = data.getPreviousLocation();
        Location to = data.getCurrentLocation();

        double thisMove = LocationHelper.distanceHorizontal(from, to);
        double baseMove = getBaseMoveSpeed(player);

        // TODO: speed check coming VERY soon.

    }

    /**
     * Return our base move speed.
     *
     * @param player
     * @return
     */
    private double getBaseMoveSpeed(Player player) {
        double baseSpeed = 0.2873;

        for (PotionEffect ef : player.getActivePotionEffects()) {
            if (ef.getType().equals(PotionEffectType.SPEED)) {
                baseSpeed *= 1.0 + 0.2 * ef.getAmplifier() + 1;
            }
        }

        return baseSpeed;
    }

    /**
     * Return if we have a speed potion active or not.
     *
     * @param player
     * @return
     */
    private boolean hasSpeedPotion(Player player) {
        for (PotionEffect ef : player.getActivePotionEffects()) {
            if (ef.getType().equals(PotionEffectType.SPEED)) {
                return true;
            }
        }

        return false;
    }

}
