package me.vrekt.arc.command;

import org.bukkit.entity.Player;

public abstract class Command {

    private String name;

    public Command(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Execute this command.
     *
     * @param args the arguments.
     */
    public abstract void execute(Player player, String[] args);

    /**
     * @return the proper usage.
     */
    public abstract String usage();

}
