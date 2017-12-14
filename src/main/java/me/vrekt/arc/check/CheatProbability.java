package me.vrekt.arc.check;

public enum CheatProbability {
    NOT_LIKELY("Not Likely"), LIKELY("Likely"), DEFINITELY("Definitely");

    private String name;

    CheatProbability(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
