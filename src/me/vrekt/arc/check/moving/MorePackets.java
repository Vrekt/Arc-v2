package me.vrekt.arc.check.moving;

import me.vrekt.arc.Arc;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.data.moving.MovingData;
import org.bukkit.entity.Player;

public class MorePackets extends Check {

    private int maxPackets, kickThreshold = 0;

    public MorePackets() {
        super(CheckType.MOREPACKETS);

        maxPackets = Arc.getCheckManager().getValueInt(CheckType.MOREPACKETS, "max-packets");
        kickThreshold = Arc.getCheckManager().getValueInt(CheckType.REGENERATION, "max-packets-kick");
    }

    public boolean check(Player player, MovingData data) {

        int packets = data.getMovingPackets();
        // check if we have sent more packets than allowed.
        if (packets > maxPackets) {
            checkViolation(player, "sending too many packets");
            if (kickThreshold > packets) {
                player.kickPlayer("You are sending too many packets.");
                return false;
            }
            data.setCancelMovingPackets(true);
            return true;
        }
        return false;
    }

}
