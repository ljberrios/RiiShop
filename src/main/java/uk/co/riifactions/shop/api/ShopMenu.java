package uk.co.riifactions.shop.api;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import uk.co.riifactions.shop.common.menu.Menu;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShopMenu extends Menu {

    private final ShopConfig config;
    private final Player holder;

    public ShopMenu(ShopConfig config, Player holder) {
        super(holder, config.getTitle(), config.getSize(), false, true);
        this.config = config;
        this.holder = holder;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        config.getShop(holder).purchase(event.getCurrentItem());
    }

    @Override
    protected void addItems() {
        config.getItems().stream()
            .filter(item -> item.getStack() != null && !holder.hasPermission(item.getPermission()))
            .forEach(item -> addItem(item.getSlot(), item.getStack()));
    }

}
