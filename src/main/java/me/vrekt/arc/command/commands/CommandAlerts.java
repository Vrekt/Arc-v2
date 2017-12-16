package me.vrekt.arc.command.commands;

import me.vrekt.arc.Arc;
import me.vrekt.arc.command.Command;
import org.bukkit.entity.Player;

public class CommandAlerts extends Command {

    public CommandAlerts() {
        super("alerts");
    }

    @Override
    public void execute(Player player, String[] args) {
        Arc.getViolationHandler().toggleAlert(player);
    }

    @Override
    public String usage() {
        return null;
    }
}
