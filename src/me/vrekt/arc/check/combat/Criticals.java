package me.vrekt.arc.check.combat;

import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.data.moving.MovingData;
import org.bukkit.entity.Player;

public class Criticals extends Check {

    public Criticals() {
        super(CheckType.CRITICALS);
    }

    public boolean check(Player player) {

        MovingData data = MovingData.getData(player);
        boolean onGround = data.isOnGround();
        if (onGround) {
            double vertical = data.getVerticalSpeed();
            // thats already impossible, lets check how high they 'jumped'.
            if (vertical < 0.37) {
                return checkViolation(player, "onGround while landing a critical.");
            }
        }
        return false;
    }
}
