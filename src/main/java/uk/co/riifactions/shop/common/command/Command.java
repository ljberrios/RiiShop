package uk.co.riifactions.shop.common.command;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Represents a command in the game.
 *
 * @author Thortex
 */
public interface Command {

    List<String> getAliases();

    void execute(CommandSender sender, String[] args);

    boolean hasPermission(CommandSender sender);

}
