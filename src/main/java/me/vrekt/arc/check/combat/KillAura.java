package me.vrekt.arc.check.combat;

import me.vrekt.arc.Arc;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.data.combat.FightData;
import me.vrekt.arc.utilties.LocationHelper;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class KillAura extends Check {

    private double maxAngle, maxReach;
    private int maxSwingTime, maxAttacks, maxAttackPackets;

    public KillAura() {
        super(CheckType.KILLAURA);

        maxAngle = Arc.getCheckManager().getValueDouble(CheckType.KILLAURA, "max-direction-angle");
        maxSwingTime = Arc.getCheckManager().getValueInt(CheckType.KILLAURA, "max-swing-time");
        maxReach = Arc.getCheckManager().getValueDouble(CheckType.KILLAURA, "max-reach");
        maxAttacks = Arc.getCheckManager().getValueInt(CheckType.KILLAURA, "max-attacks");
        maxAttackPackets = Arc.getCheckManager().getValueInt(CheckType.KILLAURA, "max-attack-packets");

    }

    public boolean check(FightData data, Player player) {
        result.reset();

        Entity last = data.getLastAttackedEntity();
        Entity entity = data.getAttackedEntity();

        // Calculate angle stuff.
        Vector playerLocation = player.getLocation().toVector();
        Vector entityLocation = entity.getLocation().toVector();
        double angle = entityLocation.subtract(playerLocation).angle(player.getLocation().getDirection());

        if (last != null && !entity.equals(last)) {
            // check the angle between the two entities
            Vector lastLocation = last.getLocation().toVector();
            double angleToLast = lastLocation.subtract(playerLocation).angle(player.getLocation().getDirection());
            double angleDifference = Math.abs(angleToLast - angle);
            double distanceDifference = LocationHelper.distance(last.getLocation(), entity.getLocation());
            if (angleDifference > maxAngle && distanceDifference <= maxReach) {
                getCheck().setCheckName("Kill Aura " + ChatColor.GRAY + "(Multi)");
                result.set(checkViolation(player, "Angle difference greater than allowed. a=" + angleDifference + " e=" + maxAngle));
            }
        }

        // check yaw difference.
        if (angle > maxAngle) {
            getCheck().setCheckName("Kill Aura " + ChatColor.GRAY + "(Direction)");
            result.set(checkViolation(player, "Angle distance greater than allowed. a=" + angle + " e=" + maxAngle));
        }

        // check swing time.
        long time = System.currentTimeMillis() - data.getLastArmSwing();
        if (time > maxSwingTime) {
            getCheck().setCheckName("Kill Aura " + ChatColor.GRAY + "(NoSwing)");
            result.set(checkViolation(player, "Player did not swing their arm in time."));
        }

        // distance to entity excluding knockback.
        double distance = LocationHelper.distance(player.getLocation(), entity.getLocation().subtract(entity.getVelocity()));
        // check for reach
        if (distance > maxReach) {
            getCheck().setCheckName("Kill Aura " + ChatColor.GRAY + "(Reach)");
            result.set(checkViolation(player, "Entity is out of players range. d=" + distance + " e=" + maxReach));
        }

        // check max attacks
        if (data.getLastAttackCheck() == 0) {
            data.setLastAttackCheck(System.currentTimeMillis());
        }
        long lastAttack = System.currentTimeMillis() - data.getLastAttackCheck();
        if (lastAttack >= 1000) {
            // check if we exceeded the max attacks in the last second.
            int totalAttacks = data.getTotalAttacks();
            if (totalAttacks > maxAttacks) {
                getCheck().setCheckName("Kill Aura " + ChatColor.GRAY + "(Speed)");
                result.set(checkViolation(player, "Attacking too fast. a=" + totalAttacks + " e=" + maxAttacks));
            }
            // reset data for next check.
            data.setTotalAttacks(0);
            data.setLastAttackCheck(System.currentTimeMillis());
        }

        getCheck().setCheckName("KillAura");
        return result.failed();
    }

    public boolean checkFrequency(FightData data, Player player) {
        if (data.getLastAttackCheck() == 0) {
            data.setLastAttackCheck(System.currentTimeMillis());
            return false;
        }

        long time = System.currentTimeMillis() - data.getLastAttackCheck();
        if (time >= 1000) {
            data.setLastAttackCheck(System.currentTimeMillis());
            int totalPackets = data.getAttackPackets();
            if (totalPackets > maxAttackPackets) {
                data.setAttackPackets(0);
                getCheck().setCheckName("Kill Aura " + ChatColor.GRAY + "(Speed)");
                return checkViolation(player, "Attacking too fast a=" + totalPackets + " e=" + maxAttackPackets);
            }
        }

        return false;
    }

}
