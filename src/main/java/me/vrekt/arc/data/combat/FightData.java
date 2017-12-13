package me.vrekt.arc.data.combat;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class FightData {
    private static final Map<Player, FightData> DATA_MAP = new HashMap<>();

    /**
     * Retrieve the players data.
     *
     * @param player the player
     * @return the data
     */
    public static FightData getData(Player player) {
        DATA_MAP.putIfAbsent(player, new FightData());
        return DATA_MAP.get(player);
    }

    /**
     * Remove the players data.
     *
     * @param player the player
     */
    public static void removeData(Player player) {
        if (DATA_MAP.containsKey(player)) {
            DATA_MAP.remove(player);
        }
    }

    private long lastHealthEvent;
    private long lastArmSwing;

    private int totalAttacks;
    private long lastAttackCheck;

    private Entity attackedEntity;
    private Entity lastAttackedEntity;

    public long getLastHealthEvent() {
        return lastHealthEvent;
    }

    public void setLastHealthEvent(long lastHealthEvent) {
        this.lastHealthEvent = lastHealthEvent;
    }

    public Entity getAttackedEntity() {
        return attackedEntity;
    }

    public void setAttackedEntity(Entity attackedEntity) {
        this.attackedEntity = attackedEntity;
    }

    public Entity getLastAttackedEntity() {
        return lastAttackedEntity;
    }

    public void setLastAttackedEntity(Entity lastAttackedEntity) {
        this.lastAttackedEntity = lastAttackedEntity;
    }

    public long getLastArmSwing() {
        return lastArmSwing;
    }

    public void setLastArmSwing(long lastArmSwing) {
        this.lastArmSwing = lastArmSwing;
    }

    public int getTotalAttacks() {
        return totalAttacks;
    }

    public void setTotalAttacks(int totalAttacks) {
        this.totalAttacks = totalAttacks;
    }

    public long getLastAttackCheck() {
        return lastAttackCheck;
    }

    public void setLastAttackCheck(long lastAttackCheck) {
        this.lastAttackCheck = lastAttackCheck;
    }
}
