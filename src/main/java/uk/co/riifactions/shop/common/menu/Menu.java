package uk.co.riifactions.shop.common.menu;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a GUI in the game.
 *
 * @author Thortex
 */
@Data
public abstract class Menu {

    protected final Player holder;

    protected final Map<Integer, ItemStack> items = new HashMap<>();

    protected Inventory inventory;
    protected boolean modifiable;
    protected boolean itemFlags;

    public Menu(Player holder, String title, Object size, boolean modifiable, boolean itemFlags) {
        this.holder = holder;
        this.modifiable = modifiable;
        this.itemFlags = itemFlags;

        if (size instanceof InventoryType) {
            inventory = Bukkit.getServer().createInventory(holder, (InventoryType) size, title);
        } else if (size instanceof Integer) {
            inventory = Bukkit.getServer().createInventory(holder, (int) size, title);
        } else {
            throw new IllegalArgumentException("size must be an InventoryType or an Integer");
        }
    }

    public void open(MenuService service) {
        addItems();
        build(service);

        holder.openInventory(inventory);
    }

    public abstract void onClick(InventoryClickEvent event);

    protected abstract void addItems();

    public void addItem(int slot, ItemStack item) {
        items.put(slot, item);
    }

    protected void build(MenuService service) {
        inventory.clear();
        items.entrySet().forEach(entry -> {
            ItemStack item = entry.getValue();
            if (itemFlags && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                meta.addItemFlags(ItemFlag.values());
                item.setItemMeta(meta);
            }
            inventory.setItem(entry.getKey(), item);
        });
        service.getMenus().add(this);
    }

}
