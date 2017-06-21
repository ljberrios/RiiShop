package uk.co.riifactions.shop.commands;

import com.google.inject.Inject;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.co.riifactions.shop.common.command.Command;
import uk.co.riifactions.shop.common.language.I18n;
import uk.co.riifactions.shop.common.menu.MenuService;
import uk.co.riifactions.shop.api.ShopConfig;
import uk.co.riifactions.shop.api.ShopService;

import java.util.Arrays;
import java.util.List;

public class ShopCommand implements Command {

    private final ShopService shops;
    private final MenuService menus;

    @Inject
    public ShopCommand(ShopService shops, MenuService menus) {
        this.shops = shops;
        this.menus = menus;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("rshop", "riishop");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        Player player = (Player) sender;
        I18n i18n = shops.getI18n();
        List<ShopConfig> shops = this.shops.getShops();

        switch (args.length) {
            case 0:
                if (!shops.isEmpty())
                    shops.get(0).getShop(player).getMenu().open(menus);
                else
                    player.sendMessage(i18n.translate("noShopsAvailable"));
                break;

            case 1:
                if (args[0].equalsIgnoreCase("list")) {
                    player.sendMessage(i18n.translate("shops") + ":");
                    shops.forEach(shop -> player.sendMessage(shop.getName()));
                    return;

                } else if (args[0].equalsIgnoreCase("help")) {
                    help(player, i18n);
                    return;
                }

                ShopConfig shop = this.shops.getShop(args[0]);
                if (shop != null)
                    shop.getShop(player).getMenu().open(menus);
                else
                    player.sendMessage(i18n.translate("shopNotFound"));
                break;

            default:
                help(player, i18n);
                break;
        }
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return true;
    }

    private void help(Player player, I18n i18n) {
        player.sendMessage(i18n.translate("shops") + " Help");
        player.sendMessage(i18n.translate("singleArgShop"));
        player.sendMessage(i18n.translate("multiArgShop"));
        player.sendMessage(i18n.translate("shopList"));
        player.sendMessage(i18n.translate("shopHelp"));
    }

}
