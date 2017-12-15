package me.vrekt.arc.check.moving;

import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.utilties.LocationHelper;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class NoFall extends Check {

    public NoFall() {
        super(CheckType.NOFALL);
    }

    public void check(Player player, MovingData data) {
        if (data.getGroundLocation() == null) {
            return;
        }

        Location location = data.getCurrentLocation();
        Location ground = data.getGroundLocation();

        double toGround = LocationHelper.distanceVertical(ground, location);
        boolean isDescending = toGround >= 3.0 && data.isDescending() && !data.isOnGround();

        // make sure we meet the req.
        if (isDescending && !data.isClimbing()) {
            boolean ccOnGround = data.isFlyingClientOnGround();
            // we're not on ground but the client is?
            if (ccOnGround && player.getFallDistance() == 0.0) {
                boolean cancel = checkViolation(player, "Fall distance is 0 while not onGround. (Client faked)");
                // if we cancel damage the player. (not accurate)
                // TODO: Make accurate
                if (cancel) {
                    player.damage(toGround);
                }
            }
        }

    }

}
