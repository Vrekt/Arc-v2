package me.vrekt.arc.exemption.data;

import me.vrekt.arc.check.CheckType;

import java.util.HashMap;
import java.util.Map;

public class ExemptionData {

    private final Map<CheckType, Long> CHECK_EXEMPTIONS = new HashMap<>();

    /**
     * @param check           the check.
     * @param timeExemptedFor how long the player is exempted for.
     */
    public ExemptionData(CheckType check, long timeExemptedFor) {
        CHECK_EXEMPTIONS.put(check, timeExemptedFor);
    }

    /**
     * Check if the check specified is exempted.
     *
     * @param check the check
     * @return if the check is exempted or not.
     */
    public boolean isCheckExempted(CheckType check) {
        return CHECK_EXEMPTIONS.containsKey(check);
    }

    /**
     * Get the time the check is exempted for.
     *
     * @param check the check
     * @return the time the check is exempted for.
     */
    public long getExemptionTime(CheckType check) {
        return CHECK_EXEMPTIONS.getOrDefault(check, (long) 0);
    }

    /**
     * Add an exemption for the check.
     *
     * @param check the check
     * @param time  the time.
     */
    public void exemptCheck(CheckType check, long time) {
        CHECK_EXEMPTIONS.put(check, time);
    }

    /**
     * Remove an exemption.
     *
     * @param check the check.
     */
    public void removeExemption(CheckType check) {
        if (CHECK_EXEMPTIONS.containsKey(check)) {
            CHECK_EXEMPTIONS.remove(check);
        }
    }

}
