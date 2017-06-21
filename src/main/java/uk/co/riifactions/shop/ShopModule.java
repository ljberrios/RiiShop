package uk.co.riifactions.shop;

import com.google.inject.AbstractModule;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopModule extends AbstractModule {

    private final JavaPlugin plugin;

    public ShopModule(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(JavaPlugin.class).toInstance(plugin);
    }

}
