package uk.co.riifactions.shop;

import com.google.inject.Guice;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopPlugin extends JavaPlugin {

    private ShopLoader loader;

    @Override
    public void onEnable() {
        loader = new ShopLoader(Guice.createInjector(new ShopModule(this)));
        loader.start();
    }

    @Override
    public void onDisable() {
        loader.stop();
    }

}