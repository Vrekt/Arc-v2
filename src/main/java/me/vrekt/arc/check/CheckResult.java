package me.vrekt.arc.check;

import org.bukkit.Location;

public class CheckResult {

    private boolean hasFailed;
    private Location cancelLocation = null;

    /**
     * Set if we have failed.
     *
     * @param hasFailed indicates if we have failed.
     */
    public void set(boolean hasFailed) {
        if (!hasFailed && this.hasFailed) {
            return;
        }
        this.hasFailed = hasFailed;
        // reset setback to prevent an incorrect cancel?
        if (hasFailed) {
            cancelLocation = null;
        }
    }

    public void set(boolean hasFailed, Location setback) {
        if (!hasFailed && this.hasFailed) {
            return;
        }
        this.hasFailed = hasFailed;
        if (hasFailed) {
            cancelLocation = setback;
        }
    }

    /**
     * @return if we have failed or not.
     */
    public boolean failed() {
        return hasFailed;
    }

    /**
     * Reset the hasFailed field.
     */
    public void reset() {
        hasFailed = false;
        cancelLocation = null;
    }

    /**
     * @return the setback location.
     */
    public Location getCancelLocation() {
        return cancelLocation;
    }
}
