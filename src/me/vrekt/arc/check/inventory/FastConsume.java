package me.vrekt.arc.check.inventory;

import me.vrekt.arc.Arc;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.data.inventory.InventoryData;
import org.bukkit.entity.Player;

public class FastConsume extends Check {

    private int consumeTime = 0;

    public FastConsume() {
        super(CheckType.FASTCONSUME);

        consumeTime = Arc.getCheckManager().getValueInt(CheckType.FASTCONSUME, "consume-time");
    }

    public boolean check(Player player, InventoryData data) {

        long now = System.currentTimeMillis();
        long last = data.getConsumeTime();
        long total = now - last;

        // check if the time it took to consume the item is less than allowed.
        return total < consumeTime && checkViolation(player);
    }
}
