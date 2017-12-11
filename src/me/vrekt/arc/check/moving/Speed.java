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

    public boolean check(Player player, MovingData data) {
        result.reset();
        Location from = data.getPreviousLocation();
        Location to = data.getCurrentLocation();

        boolean onGround = data.isOnGround();
        int groundTicks = data.getGroundTime();

        if (onGround) {
            groundTicks++;
            data.setGroundTime(groundTicks);
        }

        double thisMove = LocationHelper.distanceHorizontal(from, to);
        double baseMove = getBaseMoveSpeed(player);
        double vertical = data.getVerticalSpeed();

        if (!onGround) {
            data.setGroundTime(0);
            groundTicks = 0;
        }

        if (onGround && groundTicks >= 5) {
            // expected ground.
            double expected = thisMove / data.getGroundTime() + baseMove;

            if (thisMove > expected) {
                result.set(checkViolation(player, "Moving too fast, onground_expected m=" + thisMove + " e=" + expected));
            }

        }

        if (!onGround) {
            boolean iceLiftOff = LocationHelper.isOnIce(to);
            // handle ice jumping
            if (iceLiftOff) {
                if (thisMove > 0.518) {
                    result.set(checkViolation(player, "Moving too fast, offground_ice"));
                }
                //   player.sendMessage("THISMOVE: " + thisMove);
            }
        }

        // if we didnt fail set our setback.
        if (!result.failed()) {
            data.setSetback(to);
        }

        return result.failed();
    }

    /**
     * Return our base move speed.
     *
     * @param player the player
     * @return players move speed
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
     * @param player the player
     * @return if the player has a speed potion
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
