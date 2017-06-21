package uk.co.riifactions.shop.common.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Data;
import uk.co.riifactions.shop.common.language.I18n;
import uk.co.riifactions.shop.common.service.Service;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles commands.
 *
 * @author Thortex
 */
@Singleton
@Data
public class CommandService implements Service, Listener {

    private final JavaPlugin plugin;
    private final I18n i18n;

    private final Map<List<String>, Command> commands = new HashMap<>();

    @Inject
    public CommandService(JavaPlugin plugin, I18n i18n) {
        this.plugin = plugin;
        this.i18n = i18n;
    }

    @Override
    public void start() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    public Command getCommand(String label) {
        for (List<String> aliases : commands.keySet()) {
            if (aliases.contains(label)) {
                return commands.get(aliases);
            }
        }
        return null;
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        executeCommand(event, event.getSender(), event.getCommand());
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        executeCommand(event, event.getPlayer(), event.getMessage());
    }

    private void executeCommand(Cancellable event, CommandSender sender, String msg) {
        String label;
        String[] args;

        // If there's more than 1 argument, separate the label and retrieve the arguments
        if (msg.contains(" ")) {
            label = msg.split(" ")[0].replaceAll("/", "");
            args = msg.substring(msg.indexOf(' ') + 1).split(" ");
        } else {
            label = msg.replaceAll("/", "");
            args = new String[]{};
        }

        Command command = getCommand(label);
        if (command != null) {
            event.setCancelled(true);
            if (command.hasPermission(sender)) {
                command.execute(sender, args);
            } else {
                sender.sendMessage(i18n.translate("cmdNotAllowed"));
            }
        }
    }

}
