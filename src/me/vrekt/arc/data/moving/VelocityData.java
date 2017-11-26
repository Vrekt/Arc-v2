package me.vrekt.arc.data.moving;

public class VelocityData {
    public enum VelocityCause {
        SLIMEBLOCK, TELEPORT, KNOCKBACK
    }

    private double lastVelocity;
    private double currentVelocity;

    private boolean hasVelocity;
    private VelocityCause velocityCause;

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

    public VelocityCause getVelocityCause() {
        return velocityCause;
    }

    public void setVelocityCause(VelocityCause velocityCause) {
        this.velocityCause = velocityCause;
    }
}
