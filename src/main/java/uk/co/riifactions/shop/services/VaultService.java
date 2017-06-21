package uk.co.riifactions.shop.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Data;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.riifactions.shop.common.service.Service;

@Singleton
@Data
public class VaultService implements Service {

    private final JavaPlugin plugin;

    private Economy economy;
    private Permission permissions;

    @Inject
    public VaultService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void start() {
        if (!setupEconomy() ) {
            plugin.getLogger().severe(String.format("[%s] - Disabled due to no Vault " +
                "dependency found!", plugin.getDescription().getName()));
            Bukkit.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        setupEconomy();
        setupPermissions();
    }

    @Override
    public void stop() {}

    private boolean setupEconomy() {
        Server server = Bukkit.getServer();
        if (server.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = server.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServer()
            .getServicesManager().getRegistration(Permission.class);
        permissions = rsp.getProvider();
        return permissions != null;
    }

}
