package uk.co.riifactions.shop;

import com.google.inject.Injector;
import lombok.Data;
import uk.co.riifactions.shop.common.command.CommandLoader;
import uk.co.riifactions.shop.common.service.ServiceLoader;

@Data
public class ShopLoader {

    public static final String PACKAGE = "uk.co.riifactions.shop";

    private final Injector injector;

    private final ServiceLoader services;
    private final CommandLoader commands;

    public ShopLoader(Injector injector) {
        this.injector = injector;
        services = new ServiceLoader(injector, PACKAGE);
        commands = new CommandLoader(injector, PACKAGE);
    }

    public void start() {
        services.startAll();
        commands.startAll();
    }

    public void stop() {
        services.stopAll();
        commands.stopAll();
    }

}
