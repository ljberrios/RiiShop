package uk.co.riifactions.shop.commands;

import com.google.inject.Inject;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.co.riifactions.shop.common.command.Command;
import uk.co.riifactions.shop.common.language.I18n;
import uk.co.riifactions.shop.api.ShopConfig;
import uk.co.riifactions.shop.api.ShopService;
import uk.co.riifactions.shop.util.ItemParsing;

import java.util.Arrays;
import java.util.List;

public class ShopEditCommand implements Command {

    private final ShopService shops;

    @Inject
    public ShopEditCommand(ShopService shops) {
        this.shops = shops;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("rshopedit", "riishopedit", "rshope", "riishope");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;
        I18n i18n = shops.getI18n();

        switch (args.length) {
            case 1:
                if (args[0].equalsIgnoreCase("reload")) {
                    shops.reloadShops();
                    player.sendMessage(i18n.translate("shopsReloaded"));
                } else if (args[0].equalsIgnoreCase("help")) {
                    help(player, i18n);
                } else {
                    help(player, i18n);
                }
                break;

            case 2:
                if (args[0].equalsIgnoreCase("remove")) {
                    ShopConfig shop = shops.getShop(args[1]);
                    if (shop == null) {
                        player.sendMessage(i18n.translate("shopNotFound"));
                        return;
                    }

                    shops.getShops().remove(shop);
                    player.sendMessage(i18n.translate("shopRemoved"));
                } else {
                    help(player, i18n);
                }
                break;

            case 4:
                if (args[0].equalsIgnoreCase("add")) {
                    String name = args[1];
                    ShopConfig shop = shops.getShop(name);
                    if (shop != null) {
                        player.sendMessage(i18n.translate("shopAlreadyExists"));
                        return;
                    }

                    String title = args[2];
                    String size = args[3];
                    if (!ItemParsing.isInt(size)) {
                        player.sendMessage(i18n.translate("invalidSize"));
                        return;
                    }

                    shop = new ShopConfig(shops.getPlugin(), shops.getVault(), i18n, name);
                    shop.setName(name).setTitle(title).setSize(Integer.parseInt(size));

                    shops.getShops().add(shop);

                    player.sendMessage(i18n.translate("shopAdded"));
                } else {
                    help(player, i18n);
                }
                break;

            default:
                help(player, i18n);
                break;
        }
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("shop.edit");
    }

    public void help(Player player, I18n i18n) {
        player.sendMessage(i18n.translate("shops") + " Edit Help");
        player.sendMessage(i18n.translate("addShop"));
        player.sendMessage(i18n.translate("removeShop"));
        player.sendMessage(i18n.translate("reloadShops"));
        player.sendMessage(i18n.translate("shopEditHelp"));
    }

}
