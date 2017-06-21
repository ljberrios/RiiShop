package uk.co.riifactions.shop.common.util;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Item creation utility class.
 *
 * @author Thortex
 */
public final class Items {

    private Items() {}

    /**
     * Create a banner.
     *
     * @param baseColor the base color
     * @param patterns  the patterns
     * @param name      the name
     * @param lore      the lore
     * @return the item
     */
    public static ItemStack createBanner(DyeColor baseColor, List<Pattern> patterns, String name, String... lore) {
        ItemStack banner = createItem(Material.BANNER, name, lore);
        BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();
        bannerMeta.setBaseColor(baseColor);
        bannerMeta.setPatterns(patterns);
        banner.setItemMeta(bannerMeta);
        return banner;
    }

    /**
     * Create an item.
     *
     * @param material the material
     * @param name     the name
     * @param lore     the lore
     * @return the item
     */
    public static ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Create an item.
     *
     * @param material     the material
     * @param enchantments the enchantments
     * @param data         the data
     * @param name         the name
     * @param lore         the lore
     * @return the item
     */
    public static ItemStack createItem(Material material, Map<Enchantment, Integer> enchantments,
                                       byte data, String name, String... lore) {
        ItemStack item = createItem(material, 1, data, name, lore);
        item.addUnsafeEnchantments(enchantments);
        return item;
    }

    /**
     * Create an item.
     *
     * @param material the material
     * @param amount   the amount
     * @param data     the data
     * @return the item
     */
    public static ItemStack createItem(Material material, int amount, byte data) {
        return new ItemStack(material, amount, data);
    }

    /**
     * Create an item.
     *
     * @param material the material
     * @param amount   the amount
     * @param data     the data
     * @param name     the name
     * @param lore     the lore
     * @return the item
     */
    public static ItemStack createItem(Material material, int amount, byte data, String name, String... lore) {
        ItemStack item = new ItemStack(material, amount, data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Create a potion.
     *
     * @param amount the amount
     * @param type   the type
     * @param lvl    the level
     * @param splash if it's a splash potion
     * @param name   the name
     * @param lore   the lore
     * @return the item
     */
    public static ItemStack createPotion(int amount, PotionType type, int lvl,
                                         boolean splash, String name, String... lore) {
        Potion potion = new Potion(type);
        potion.setLevel(lvl);
        potion.setSplash(splash);

        ItemStack item = potion.toItemStack(amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Create a monster egg.
     *
     * @param entityType the entity type
     * @param amount     the amount
     * @param name       the name
     * @param lore       the lore
     * @return the item
     */
    public static ItemStack createEgg(EntityType entityType, int amount, String name, String... lore) {
        return createItem(Material.MONSTER_EGG, amount, (byte) entityType.ordinal(), name, lore);
    }

    /**
     * Create a skull.
     *
     * @param type   the type
     * @param amount the amount
     * @param name   the name
     * @param lore   the lore
     * @return the item
     */
    public static ItemStack createSkull(SkullType type, int amount, String name, String... lore) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, amount, (byte) type.ordinal());
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        skull.setItemMeta(meta);
        return skull;
    }

    /**
     * Create a player skull.
     *
     * @param playerName the player name
     * @param amount     the amount
     * @param name       the name
     * @param lore       the lore
     * @return the item
     */
    public static ItemStack createPlayerHead(String playerName, int amount, String name, String... lore) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, amount, (byte) SkullType.PLAYER.ordinal());
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner(playerName);
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        skull.setItemMeta(meta);
        return skull;
    }

    /**
     * Verify an item.
     *
     * @param item     the item
     * @param name     the name it's supposed to have
     * @param material the material it's supposed to have
     * @return if the item has the given name and material
     */
    public static boolean checkItem(ItemStack item, String name, Material material) {
        return item != null
            && item.getType() == material
            && item.hasItemMeta()
            && item.getItemMeta().hasDisplayName()
            && item.getItemMeta().getDisplayName().contains(name);
    }

}
