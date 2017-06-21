package uk.co.riifactions.shop.api;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public class ShopItem {

    private final ItemStack stack;
    private final String permission;
    private final double price;
    private final int slot;

}
