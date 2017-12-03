package me.vrekt.arc.check;

public class CheckResult {

    private boolean hasFailed;

    public void set(boolean hasFailed) {
        if (this.hasFailed) {
            return;
        }
        this.hasFailed = hasFailed;
    }

    public boolean failed() {
        return hasFailed;
    }

    public void reset() {
        hasFailed = false;
    }

}
