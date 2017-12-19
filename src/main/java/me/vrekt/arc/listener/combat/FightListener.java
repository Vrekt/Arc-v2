package me.vrekt.arc.listener.combat;

import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.combat.Criticals;
import me.vrekt.arc.check.combat.KillAura;
import me.vrekt.arc.check.combat.Regeneration;
import me.vrekt.arc.data.combat.FightData;
import me.vrekt.arc.listener.ACheckListener;
import me.vrekt.arc.utilties.FightHelper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class FightListener implements Listener, ACheckListener {
    private final Regeneration REGENERATION = (Regeneration) Arc.getCheckManager().getCheck(CheckType.REGENERATION);
    private final Criticals CRITICALS = (Criticals) Arc.getCheckManager().getCheck(CheckType.CRITICALS);
    private final KillAura KILL_AURA = (KillAura) Arc.getCheckManager().getCheck(CheckType.KILLAURA);

    @EventHandler(priority = EventPriority.HIGHEST)
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFight(EntityDamageByEntityEvent event) {

        Entity attacked = event.getEntity();
        Entity damager = event.getDamager();

        // check if the player attacked an entity.
        if (damager instanceof Player) {
            Player player = (Player) damager;
            FightData data = FightData.getData(player);

            data.setLastAttackedEntity(data.getAttackedEntity());
            data.setAttackedEntity(attacked);

            // make sure the attack is a critical.
            if (FightHelper.isCritical(player)) {
                // it was, make sure we are not exempt and check.
                boolean canCheckCriticals = Arc.getCheckManager().canCheckPlayer(player, CheckType.CRITICALS);
                if (canCheckCriticals) {
                    boolean cancel = CRITICALS.check(player);
                    if (cancel) {
                        event.setCancelled(true);
                    }
                }
            }

            // make sure we can check direction.
            boolean canCheckKillAura = Arc.getCheckManager().canCheckPlayer(player, CheckType.KILLAURA);
            if (canCheckKillAura) {
                boolean cancel = KILL_AURA.check(data, player);
                if (cancel) {
                    event.setCancelled(true);
                }
            }

        }

    }

}
