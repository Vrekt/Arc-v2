package me.vrekt.arc.check.combat;

import me.vrekt.arc.Arc;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.data.combat.FightData;
import org.bukkit.entity.Player;

public class Regeneration extends Check {
    private long regenerationTime = 0;

    public Regeneration() {
        super(CheckType.REGENERATION);

        regenerationTime = (long) Arc.getCheckManager().getValueInt(CheckType.REGENERATION, "regen-time");
    }

    public boolean check(Player player, FightData data) {
        if (data.getLastHealthEvent() == 0) {
            return false;
        }

        long now = System.currentTimeMillis();
        long past = data.getLastHealthEvent();
        long timeSince = now - past;

        // check if the time it took to regenerate is lower than allowed.
        return timeSince < regenerationTime && checkViolation(player);
    }

}
