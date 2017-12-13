package me.vrekt.arc.utilties;

import me.vrekt.arc.Arc;

public class Timings {

    private long currentTime;

    public void start() {
        currentTime = System.nanoTime();
    }

    public void stop() {
        long time = System.nanoTime() - currentTime;
        Arc.getPlugin().getLogger().info("TIME FOR CHECK TO RUN: " + time);
    }

    public long get() {
        return System.nanoTime() - currentTime;
    }
}
