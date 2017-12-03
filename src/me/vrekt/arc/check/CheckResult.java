package me.vrekt.arc.check;

public class CheckResult {

    private boolean hasFailed;

    /**
     * Set if we have failed.
     *
     * @param hasFailed indicates if we have failed.
     */
    public void set(boolean hasFailed) {
        if (this.hasFailed) {
            return;
        }
        this.hasFailed = hasFailed;
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
    }

}
