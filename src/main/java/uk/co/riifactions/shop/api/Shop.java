package uk.co.riifactions.shop.api;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import uk.co.riifactions.shop.common.language.I18n;
import uk.co.riifactions.shop.services.VaultService;

import java.lang.ref.WeakReference;

@Data
public class Shop {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final WeakReference<Player> holder;
    private final ShopConfig config;
    private final VaultService vault;
    private final I18n i18n;

    private ShopMenu menu;

    public Shop(Player holder, ShopConfig config, VaultService vault, I18n i18n) {
        this.holder = new WeakReference<>(holder);
        this.config = config;
        this.vault = vault;
        this.i18n = i18n;
    }

    public void purchase(ItemStack stack) {
        Preconditions.checkArgument(stack != null, "item must not be null");

        Player holder = this.holder.get();
        ShopItem item = toShopItem(stack);

        Economy economy = vault.getEconomy();

        if (holder == null) return;

        if (item == null) {
            holder.closeInventory();
            holder.sendMessage(i18n.translate("itemNotAvailable"));
            return;
        }

        double balance = economy.getBalance(holder);
        double price = item.getPrice();
        if (balance < price) {
            holder.closeInventory();
            holder.sendMessage(i18n.translate("notEnoughFunds"));
            return;
        }

        economy.withdrawPlayer(holder, price);

        vault.getPermissions().playerAdd(holder, item.getPermission());

        holder.closeInventory();
        holder.sendMessage(i18n.translate("purchaseComplete"));
    }

    public ShopMenu getMenu() {
        if (menu == null)
            menu = new ShopMenu(config, holder.get());
        return menu;
    }

    public ShopItem toShopItem(ItemStack from) {
        for (ShopItem to : config.getItems()) {
            ItemStack stack = to.getStack();
            if (stack != null && stack.isSimilar(from))
                return to;
        }
        return null;
    }

    public Player getHolder() {
        return holder.get();
    }

}
