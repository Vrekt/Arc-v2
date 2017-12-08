package me.vrekt.arc.command;

import me.vrekt.arc.command.commands.CommandInfo;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandExecutor {

    private final List<Command> COMMAND_LIST = new ArrayList<>();

    public CommandExecutor() {
        // add commands
        COMMAND_LIST.add(new CommandInfo());
    }

    /**
     * @param player the player
     * @param name   the command name
     * @param args   the arguments.
     * @return true if the command was executed.
     */
    public boolean executeCommand(Player player, String name, String[] args) {

        Command command = COMMAND_LIST.stream().filter(cmd -> cmd.getName().equalsIgnoreCase(name)).findAny().orElse(null);
        if (command == null) {
            return false;
        }

        command.execute(player, args);
        return true;
    }

}
