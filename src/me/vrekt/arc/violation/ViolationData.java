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
     * Clear violation data.
     */
    public void clearData() {
        CHECK_VIOLATIONS.clear();
    }

}
