package me.vrekt.arc.check;

public enum CheckType {

    FLIGHT(CheckCategory.MOVEMENT, "Flight"),
    SPEED(CheckCategory.MOVEMENT, "Speed"),
    NOFALL(CheckCategory.MOVEMENT, "NoFall"),
    MOREPACKETS(CheckCategory.MOVEMENT, "MorePackets"),
    CRITICALS(CheckCategory.COMBAT, "Criticals"),
    DIRECTION(CheckCategory.COMBAT, "Direction"),
    IMPROBABLE(CheckCategory.COMBAT, "Combat Improbable"),
    NOSWING(CheckCategory.COMBAT, "NoSwing"),
    REACH(CheckCategory.COMBAT, "Reach"),
    REGENERATION(CheckCategory.COMBAT, "Regeneration");

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

}
