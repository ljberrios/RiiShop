package uk.co.riifactions.shop.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.riifactions.shop.common.language.I18n;
import uk.co.riifactions.shop.common.service.Service;
import uk.co.riifactions.shop.services.VaultService;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Singleton
@Data
public class ShopService implements Service {

    private final JavaPlugin plugin;
    private final VaultService vault;
    private final I18n i18n;

    private final List<ShopConfig> shops = new ArrayList<>();
    private final File directory;

    @Inject
    public ShopService(JavaPlugin plugin, VaultService vault, I18n i18n) {
        this.plugin = plugin;
        this.vault = vault;
        this.i18n = i18n;

        directory = new File(plugin.getDataFolder(), "shops");
    }

    @Override
    public void start() {
        reloadShops();
    }

    @Override
    public void stop() {
        shops.clear();
    }

    public ShopConfig getShop(String name) {
        for (ShopConfig shop : shops) {
            if (shop.getName().equalsIgnoreCase(name))
                return shop;
        }
        return null;
    }

    public void reloadShops() {
        Logger logger = plugin.getLogger();
        logger.info("Loading shops...");

        shops.clear();

        if (directory.exists() && directory.isDirectory() && directory.listFiles().length > 0)
            Arrays.stream(directory.listFiles())
                .filter(file -> file.isFile() && file.getName().contains(".yml"))
                .forEach(file -> {
                    String name = file.getName().replaceAll(".yml", "");
                    ShopConfig shop = new ShopConfig(plugin, vault, i18n, name);
                    registerPermissions(shop);
                    shops.add(shop);
                    logger.info("Loaded shop '" + shop.getName() + "'");
                });
        else
            logger.info("No shops were loaded");
    }

    private void registerPermissions(ShopConfig shop) {
        PluginManager manager = Bukkit.getPluginManager();
        shop.getItems().stream()
            .filter(item -> manager.getPermission(item.getPermission()) == null)
            .forEach(item -> manager.addPermission(new Permission(item.getPermission())));
    }

}
