package me.vrekt.arc.violation;

import me.vrekt.arc.check.CheckType;

import java.util.HashMap;
import java.util.Map;

public class ViolationData {

    private final Map<CheckType, Integer> CHECK_VIOLATIONS = new HashMap<>();

    /**
     * @param check the check.
     * @return how many times the player has failed the check.
     */
    public int getViolationLevel(CheckType check) {
        return CHECK_VIOLATIONS.getOrDefault(check, 0);
    }

    /**
     * Increment the violation level.
     *
     * @param check the check.
     */
    public void incrementViolationLevel(CheckType check) {
        CHECK_VIOLATIONS.put(check, getViolationLevel(check) + 1);
    }

    /**
     * Remove violation level for a certain check.
     *
     * @param check the check.
     */
    public void removeViolationsForCheck(CheckType check) {
        if (CHECK_VIOLATIONS.containsKey(check)) {
            CHECK_VIOLATIONS.remove(check);
        }
    }

    /**
     * @return all checks the player has failed.
     */
    public Map<CheckType, Integer> getViolatedChecks() {
        final Map<CheckType, Integer> VIOLATION_INFO = new HashMap<>();

        for (CheckType check : CHECK_VIOLATIONS.keySet()) {
            int violationLevel = CHECK_VIOLATIONS.get(check);
            if (violationLevel > 0) {
                VIOLATION_INFO.put(check, violationLevel);
            }
        }
        return VIOLATION_INFO;
    }

    /**
     * Clear violation data.
     */
    public void clearData() {
        CHECK_VIOLATIONS.clear();
    }

    /**
     * @return the total VL level of all checks.
     */
    public int getTotalLevel() {
        int level = 0;
        for (int i : CHECK_VIOLATIONS.values()) {
            level += i;
        }
        return level + CHECK_VIOLATIONS.keySet().size();
    }

}
