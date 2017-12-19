package me.vrekt.arc.check.moving;

import me.vrekt.arc.Arc;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.data.moving.MovingData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MorePackets extends Check {

    private int maxPackets, kickThreshold = 0;

    public MorePackets() {
        super(CheckType.MOREPACKETS);

        maxPackets = Arc.getCheckManager().getValueInt(CheckType.MOREPACKETS, "max-packets");
        kickThreshold = Arc.getCheckManager().getValueInt(CheckType.MOREPACKETS, "max-packets-kick");
    }

    public boolean check(Player player, MovingData data) {

        int flyingPackets = data.getFlyingPackets();
        int movingPackets = data.getPositionPackets();

        // check if we are sending more packets then allowed.
        if (flyingPackets > maxPackets || movingPackets > maxPackets) {
            checkViolation(player, "sending too many packets");
            if (flyingPackets > kickThreshold || movingPackets > kickThreshold) {
                // we are sending over the limit, kick.
                Bukkit.getScheduler().runTaskLater(Arc.getPlugin(), () -> {
                    // broadcast to staff.
                    Bukkit.broadcast(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Arc" + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE
                            + player.getName() + ChatColor.WHITE + " was kicked for sending too many packets. ", "arc.violations");
                    // prevent async player kick.
                    player.kickPlayer("You are sending too many packets.");
                }, 5);
                return false;
            }
            data.setCancelMovingPackets(true);
            return true;
        }

        data.setCancelMovingPackets(false);
        return false;
    }

}
