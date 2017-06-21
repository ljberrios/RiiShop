package uk.co.riifactions.shop.common.menu;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Data;
import uk.co.riifactions.shop.common.service.Service;
import uk.co.riifactions.shop.common.update.UpdateEvent;
import uk.co.riifactions.shop.common.update.UpdateType;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Handles all menus.
 *
 * @author Thortex
 */
@Singleton
@Data
public class MenuService implements Service, Listener {

    private final JavaPlugin plugin;
    private final Set<Menu> menus = new HashSet<>();

    @Inject
    public MenuService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void start() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void stop() {
        menus.stream()
            .filter(Objects::nonNull)
            .forEach(menu -> menu.getInventory().getViewers().forEach(HumanEntity::closeInventory));
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (event.getType() == UpdateType.TICK) {
            menus.removeIf(menu -> {
                List<HumanEntity> viewers = menu.getInventory().getViewers();
                return viewers == null || viewers.isEmpty();
            });
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Inventory inventory = event.getClickedInventory();
        if (inventory != null && event.getSlotType() != InventoryType.SlotType.OUTSIDE &&
            (item != null && item.getType() != Material.AIR) && inventory.getTitle() != null) {
            callInventoryEvent(inventory, event);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory != null && inventory.getTitle() != null && event.getCursor() != null) {
            callInventoryEvent(inventory, event);
        }
    }

    private <T extends Cancellable> void callInventoryEvent(Inventory inventory, T event) {
        for (Iterator<Menu> itr = menus.iterator(); itr.hasNext(); ) {
            Menu menu = itr.next();
            if (!Objects.equals(inventory, menu.getInventory())) {
                continue;
            }

            event.setCancelled(!menu.isModifiable());
		
            if (event instanceof InventoryClickEvent) {
                menu.onClick((InventoryClickEvent) event);
            }
        }
    }

}
