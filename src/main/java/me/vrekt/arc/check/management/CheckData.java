package me.vrekt.arc.check.management;

public class CheckData {

    private int notify, cancel, ban;
    private boolean cancelCheck, banCheck;

    /**
     * @param notify      the notify level.
     * @param cancel      the cancel level.
     * @param ban         the ban level.
     * @param cancelCheck if we should cancel.
     * @param banCheck    if we should ban.
     */
    public CheckData(int notify, int cancel, int ban, boolean cancelCheck, boolean banCheck) {
        this.notify = notify;
        this.cancel = cancel;
        this.ban = ban;

        this.cancelCheck = cancelCheck;
        this.banCheck = banCheck;
    }

    /**
     * @return the notify level.
     */
    public int getNotify() {
        return notify;
    }

    /**
     * @return the cancel level.
     */
    public int getCancel() {
        return cancel;
    }

    /**
     * @return the ban level.
     */
    public int getBan() {
        return ban;
    }

    /**
     * @return if we should cancel or not.
     */
    public boolean shouldCancelCheck() {
        return cancelCheck;
    }

    /**
     * @return if we should ban or not.
     */
    public boolean shouldBanCheck() {
        return banCheck;
    }
}
