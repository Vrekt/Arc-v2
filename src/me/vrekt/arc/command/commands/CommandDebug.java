package me.vrekt.arc.command.commands;

import me.vrekt.arc.Arc;
import me.vrekt.arc.command.Command;
import org.bukkit.entity.Player;

public class CommandDebug extends Command {

    public CommandDebug() {
        super("debug");
    }

    @Override
    public void execute(Player player, String[] args) {
        Arc.getViolationHandler().addOrRemoveListener(player);
    }

    @Override
    public String usage() {
        return "/arc debug";
    }
}
