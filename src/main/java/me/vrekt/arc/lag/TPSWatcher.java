package me.vrekt.arc.lag;

import com.comphenix.protocol.ProtocolLibrary;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import org.bukkit.scheduler.BukkitRunnable;

public class TPSWatcher extends BukkitRunnable {

    private final ReflectionTick TICKS_PER_SECOND = new ReflectionTick();
    private long lastTickCheck = System.currentTimeMillis();

    private boolean hasDisabledChecks = false;
    private double[] previousTPS = new double[4];
    private int tpsIndex = 0;

    @Override
    public void run() {
        if (System.currentTimeMillis() - lastTickCheck >= 10000) {
            lastTickCheck = System.currentTimeMillis();
            double tps = TICKS_PER_SECOND.getTPS();
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
