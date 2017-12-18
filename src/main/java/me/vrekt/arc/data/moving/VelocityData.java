package me.vrekt.arc.data.moving;

import java.util.ArrayList;
import java.util.List;

public class VelocityData {
    private double lastVelocity;
    private double currentVelocity;
    private boolean hasVelocity;

    private final List<Double> slimeblockVelocity = new ArrayList<>();

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
        slimeblockVelocity.add(difference);
    }

    public void clear() {
        slimeblockVelocity.clear();
    }

    public List<Double> getSlimeblockVelocity() {
        return slimeblockVelocity;
    }
}
