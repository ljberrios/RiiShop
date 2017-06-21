package uk.co.riifactions.shop.common.command;

import com.google.inject.Injector;
import com.google.inject.Singleton;
import uk.co.riifactions.shop.common.AbstractLoader;

import java.util.List;
import java.util.Map;

/**
 * Handles command loading/unloading functionality.
 *
 * @author Thortex
 */
@Singleton
public class CommandLoader extends AbstractLoader<Command> {

    private final Injector injector;

    public CommandLoader(Injector injector, String packageName) {
        super(injector, packageName, Command.class);
        this.injector = injector;
    }

    @Override
    public void startAll() {
        getInstances().forEach(cmd -> getCommands().put(cmd.getAliases(), cmd));
    }

    @Override
    public void stopAll() {
        getInstances().forEach(cmd -> getCommands().remove(cmd.getAliases()));
    }

    public Map<List<String>, Command> getCommands() {
        return injector.getInstance(CommandService.class).getCommands();
    }

}
