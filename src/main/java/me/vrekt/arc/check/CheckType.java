package me.vrekt.arc.check;

public enum CheckType {

    FLIGHT(CheckCategory.MOVEMENT, "Flight"),
    SPEED(CheckCategory.MOVEMENT, "Speed"),
    NOFALL(CheckCategory.MOVEMENT, "NoFall"),
    MOREPACKETS(CheckCategory.MOVEMENT, "MorePackets"),
    CRITICALS(CheckCategory.FIGHT, "Criticals"),
    NOSWING(CheckCategory.FIGHT, "NoSwing"),
    REACH(CheckCategory.FIGHT, "Reach"),
    REGENERATION(CheckCategory.FIGHT, "Regeneration"),
    FASTCONSUME(CheckCategory.INVENTORY, "FastConsume"),
    FLIGHT_17(CheckCategory.MOVEMENT, "Flight"),
    SPEED_17(CheckCategory.MOVEMENT, "Speed"),
    KILLAURA(CheckCategory.FIGHT, "KillAura");

    private CheckCategory cat;
    private String checkName;

    /**
     * Initialize a new CheckType.
     *
     * @param cat the category.
     */
    CheckType(CheckCategory cat, String checkName) {
        this.cat = cat;
        this.checkName = checkName;
    }

    /**
     * @return the check category.
     */
    public CheckCategory getCheckCategory() {
        return cat;
    }

    /**
     * @return the check name.
     */
    public String getCheckName() {
        return checkName;
    }

    /**
     * Set the checkname.
     */
    public void setCheckName(String checkName) {
        this.checkName = checkName;
    }
}
