package me.vrekt.arc.data.moving;

import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VelocityData {
    private double lastVelocity;
    private double currentVelocity;
    private boolean hasVelocity;

    private final List<Double> SB_VELOCITY = new ArrayList<>();
    private final Map<Long, Vector> VELOCITY_LOG = new HashMap<>();

    public double getLastVelocity() {
        return lastVelocity;
    }

    public void setLastVelocity(double lastVelocity) {
        this.lastVelocity = lastVelocity;
    }

    public double getCurrentVelocity() {
        return currentVelocity;
    }

    public void setCurrentVelocity(double currentVelocity) {
        this.currentVelocity = currentVelocity;
    }

    public boolean hasVelocity() {
        return hasVelocity;
    }

    public void setHasVelocity(boolean hasVelocity) {
        this.hasVelocity = hasVelocity;
    }

    public void addVelocity(double difference) {
        SB_VELOCITY.add(difference);
    }

    public void clear() {
        SB_VELOCITY.clear();
    }

    public List<Double> getSlimeblockVelocity() {
        return SB_VELOCITY;
    }

    public void addVelocityTime(Vector vel) {
        VELOCITY_LOG.put(System.currentTimeMillis(), vel);
    }

    public Vector getRecentVelocity(long withinBounds) {
        long now = System.currentTimeMillis();
        Optional<Long> times = VELOCITY_LOG.keySet().stream().filter(time -> (time - now) <= withinBounds).findAny();
        return times.map(VELOCITY_LOG::get).orElse(null);
    }

    public void removeVelocity(Vector vel) {
        Optional<Long> vec = VELOCITY_LOG.keySet().stream().filter(v -> VELOCITY_LOG.get(v).equals(vel)).findAny();
        vec.ifPresent(aLong -> VELOCITY_LOG.remove(aLong, VELOCITY_LOG.get(aLong)));
    }

}
