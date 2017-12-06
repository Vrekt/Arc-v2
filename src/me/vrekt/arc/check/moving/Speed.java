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
    private final double MAX_SPEED_JUMP_BOOST = 0.6122;
    private final double MAX_SPEED_JUMP = 0.58499359761931193;


    public Speed() {
        super(CheckType.SPEED);
    }

    public boolean check(Player player, MovingData data) {
        result.reset();
        Location from = data.getPreviousLocation();
        Location to = data.getCurrentLocation();
        Location setback = data.getSetback();

        boolean onGround = data.isOnGround();
        int groundTicks = data.getGroundTime();

        double thisMove = LocationHelper.distance(from, to);
        double baseMove = getBaseMoveSpeed(player);
        double vertical = data.getVerticalSpeed();

        if (onGround && groundTicks > 1) {
            double expected = thisMove / data.getGroundTime() + baseMove;

            // player.sendMessage("MOVE: " + thisMove + " EX: " + expected);
            if (thisMove > expected) {
                // result.set(checkViolation(player, "Moving too fast.", setback));
            }

            // check if we are jumping with a block above us
            if (vertical > 0.0 && LocationHelper.isUnderBlock(to)) {
                expected = thisMove / data.getGroundTime() + MAX_SPEED_JUMP - vertical;
                player.sendMessage("EX: " + expected + " MOVE: " + thisMove);
            }

        }

        if (!result.failed()) {
            data.setSetback(to);
        }

        return result.failed();
        //  player.sendMessage("MOVE: " + thisMove);

        // onGround checks, these include normal speed checks, ice checks, etc.
       /* if (onGround) {
            boolean hasGround = groundTime >= 3;
            if (hasGround) {
                if (thisMove > baseMove) {
                    failed = checkViolation(player, "Moving too fast.", setback);
                }
            }
            data.setGroundTime(groundTime >= 8 ? 8 : groundTime + 1);
        } else {
            data.setGroundTime(0);
        }

        // offground checks, includes hop checks, ice, etc.
        if (!onGround) {

            // calculate current take-off stage.
            double fromGround = LocationHelper.distanceVertical(data.getGroundLocation(), to);
            // fromGround should be around 0.41-0.42 (a normal jump) when we get a speed boost. (0.61)
            // generally, this is the max jump-move (0.58499359761931193) but sometimes you get boosted up to 0.61

            // "boost jump"
            if (fromGround <= 0.42) {
                if (thisMove > MAX_SPEED_JUMP_BOOST) {
                    failed = checkViolation(player, "Moving too fast.", setback);
                }
            } else if (fromGround > 0.42) {
                if (thisMove > MAX_SPEED_JUMP) {
                    failed = checkViolation(player, "Moving too fast.", setback);
                }
            }
        }

        // if we didn't fail, set our safe location to current.
        if (!failed && onGround) {
            data.setSetback(from);
        }*/
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
