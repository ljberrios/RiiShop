package uk.co.riifactions.shop.api;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.riifactions.shop.common.config.Configuration;
import uk.co.riifactions.shop.common.language.I18n;
import uk.co.riifactions.shop.services.VaultService;
import uk.co.riifactions.shop.util.ItemParsing;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class ShopConfig extends Configuration {

    private final VaultService vault;
    private final I18n i18n;

    private Shop shop;

    public ShopConfig(JavaPlugin plugin, VaultService vault, I18n i18n, String fileName) {
        super(plugin, new File(plugin.getDataFolder() + File.separator + "shops", fileName + ".yml"));
        this.vault = vault;
        this.i18n = i18n;
    }

    public Shop getShop(Player holder) {
        if (shop == null)
            shop = new Shop(holder, this, vault, i18n);
        return shop;
    }

    public String getName() {
        return config.getString("name", "Shop");
    }

    public ShopConfig setName(String name) {
        set("name", name, true);
        return this;
    }

    public int getSize() {
        return config.getInt("size", 54);
    }

    public ShopConfig setSize(int size) {
        set("size", size, true);
        return this;
    }

    public String getTitle() {
        return ChatColor.translateAlternateColorCodes('&',
            config.getString("title", "Shop")).replaceAll("_", " ");
    }

    public ShopConfig setTitle(String title) {
        set("title", title, true);
        return this;
    }

    public List<ShopItem> getItems() {
        return config.getStringList("items").stream()
            .map(this::parseItem)
            .collect(Collectors.toList());
    }

    private ShopItem parseItem(String toParse) {
        ItemParsing.MetaItemStack meta = ItemParsing.parseItemStack(toParse);
        return new ShopItem(meta.getStack(), meta.getPermission(), meta.getPrice(), meta.getSlot());
    }

}
