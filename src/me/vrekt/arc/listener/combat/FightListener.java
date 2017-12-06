package me.vrekt.arc.listener.combat;

import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.combat.Regeneration;
import me.vrekt.arc.data.combat.FightData;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.data.moving.VelocityData;
import me.vrekt.arc.listener.ACheckListener;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class FightListener implements Listener, ACheckListener {
    private final Regeneration REGENERATION = (Regeneration) Arc.getCheckManager().getCheck(CheckType.REGENERATION);


    @EventHandler
    public void onRegen(EntityRegainHealthEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        EntityRegainHealthEvent.RegainReason reason = event.getRegainReason();
        if (reason == EntityRegainHealthEvent.RegainReason.SATIATED) {
            FightData data = FightData.getData(player);
            boolean canCheckRegeneration = CHECK_MANAGER.canCheckPlayer(player, CheckType.REGENERATION);
            if (canCheckRegeneration) {
                event.setCancelled(REGENERATION.check(player, data));
                data.setLastHealthEvent(System.currentTimeMillis());
            }
        }

    }

    @EventHandler
    public void onFight(EntityDamageByEntityEvent event) {

        Entity attacked = event.getEntity();
        if (attacked instanceof Player) {
            Player player = (Player) attacked;
            MovingData data = MovingData.getData(player);
            data.getVelocityData().setHasVelocity(true);
            data.getVelocityData().setVelocityCause(VelocityData.VelocityCause.KNOCKBACK);
        }

    }

}
