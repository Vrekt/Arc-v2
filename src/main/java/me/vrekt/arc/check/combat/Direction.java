package me.vrekt.arc.check.combat;

import me.vrekt.arc.Arc;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Direction extends Check {

    private double maxYawDifference, maxPitchDifference;

    public Direction() {
        super(CheckType.DIRECTION);

        maxYawDifference = Arc.getCheckManager().getValueDouble(CheckType.DIRECTION, "max-yaw");
        maxPitchDifference = Arc.getCheckManager().getValueDouble(CheckType.DIRECTION, "max-pitch");
    }

    public boolean check(Entity entity, Player player) {
        // calculate angle.
        double y = player.getLocation().getY() + player.getEyeHeight();
        double dX = entity.getLocation().getX() - player.getLocation().getX();
        double dY = entity.getLocation().getY() - y;
        double dZ = entity.getLocation().getZ() - player.getLocation().getZ();

        // get the yaw required to aim at the entity and subtract.
        double distance = (float) Math.sqrt(dX * dX + dZ * dZ);
        float yaw = (float) (Math.atan2(dZ, dX) * 180.0D / Math.PI) - 90.0F;
        float yawDifference = Math.abs(player.getLocation().getYaw() - yaw);

        // check yaw difference.
        if (yawDifference > maxYawDifference) {
            return checkViolation(player, "Yaw difference greater than allowed.");
        }

        // get the pitch required to aim at the entity and subtract.
        float pitch = (float) -(Math.atan2(dY, distance) * 180.0D / Math.PI);
        float pitchDifference = Math.abs(player.getLocation().getPitch() - pitch);

        // check pitch difference.
        if (pitchDifference > maxPitchDifference) {
            return checkViolation(player, "Pitch difference greater than allowed.");
        }
        return false;
    }

}
