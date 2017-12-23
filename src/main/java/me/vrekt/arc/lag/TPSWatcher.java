package me.vrekt.arc.lag;

import com.comphenix.protocol.ProtocolLibrary;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import org.bukkit.scheduler.BukkitRunnable;

public class TPSWatcher extends BukkitRunnable {

    private long lastTickCheck = System.currentTimeMillis();

    private long second, currentTime;
    private int tickCount, tpsIndex;
    private double tps = 0.0;
    private double[] previousTPS = new double[4];

    private boolean hasDisabledChecks = false;

    @Override
    public void run() {

        long now = System.currentTimeMillis();
        second = (now / 1000);

        if (currentTime == second) {
            tickCount++;
        } else {
            currentTime = second;
            tps = (tps == 0 ? tickCount : ((tps + tickCount) / 2));
            tickCount = 0;
        }

        if (System.currentTimeMillis() - lastTickCheck >= 10000) {
            lastTickCheck = System.currentTimeMillis();
            tps = Math.min(tps, 20);

            // check out TPS.
            int tpsThreshold = Arc.getArcConfiguration().getTpsLimit();
            if (tps + 0.02 < tpsThreshold) {
                // We are below the limit, disable a few things.
                Arc.getPacketListener().stopListening();
                Arc.getCheckManager().disableChecks(CheckType.FLIGHT, CheckType.SPEED, CheckType.KILLAURA);
                hasDisabledChecks = true;
            }

            if (hasDisabledChecks) {
                // check if we should enable these back.
                if (tps > tpsThreshold + 0.99) {
                    // TPS has stabilized a little bit, lets log this TPS.
                    previousTPS[tpsIndex] = tps;
                    tpsIndex++;

                    // reset our index.
                    if (tpsIndex == 3) {
                        tpsIndex = 0;
                        // get the total difference and determine if we are stable enough.
                        double totalDifference = previousTPS[3] - previousTPS[0];
                        if (totalDifference >= 2.6) {
                            // enable all the checks.
                            Arc.getCheckManager().enableAllDisableChecks();
                            // enable the packet listener again.
                            Arc.getPacketListener().startListening(Arc.getPlugin(), ProtocolLibrary.getProtocolManager());
                        }
                    }
                }
            }
        }
    }
}
