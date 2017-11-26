package me.vrekt.arc.check.moving;

import me.vrekt.arc.Arc;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.data.moving.MovingData;
import org.bukkit.entity.Player;

public class MorePackets extends Check {

    private int maxPackets = 0;

    public MorePackets() {
        super(CheckType.MOREPACKETS);

        maxPackets = Arc.getCheckManager().getValueInt(CheckType.MOREPACKETS, "max-packets");

    }

    public boolean check(Player player, MovingData data) {

        int packets = data.getMovingPackets();
        // check if we have sent more packets than allowed.
        if (packets > maxPackets) {
            data.setCancelMovingPackets(true);
            checkViolation(player, "Sending too many packets. packets=" + packets + " max=" + maxPackets);
            return true;
        }
        return false;
    }

}
