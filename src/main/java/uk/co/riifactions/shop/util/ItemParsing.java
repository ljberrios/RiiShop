package uk.co.riifactions.shop.util;

import com.google.common.base.Joiner;
import lombok.Data;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import uk.co.riifactions.shop.util.lookup.Enchantments;
import uk.co.riifactions.shop.util.lookup.Potions;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ItemParsing {

    private ItemParsing() {}

    private static final Pattern splitPattern = Pattern.compile("((.*)[:+',;.](\\d+))");

    private static final Map<String, DyeColor> colorMap = new HashMap<>();
    private static final Map<String, FireworkEffect.Type> fireworkShape = new HashMap<>();

    static {
        for (DyeColor color : DyeColor.values()) {
            colorMap.put(color.name(), color);
        }
        for (FireworkEffect.Type type : FireworkEffect.Type.values()) {
            fireworkShape.put(type.name(), type);
        }
    }

    public static MetaItemStack parseItemStack(String toParse) {
        ItemStack item;
        String[] parts = toParse.split(" ");
        int amount = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;

        String id;
        short data = 0;
        Matcher dataParts = splitPattern.matcher(parts[0]);
        if (dataParts.matches()) {
            id = dataParts.group(2);
            data = Short.parseShort(dataParts.group(3));
        } else {
            id = parts[0];
        }

        if (isInt(id)) {
            item = new ItemStack(Integer.parseInt(id), amount, data);
        } else {
            item = new ItemStack(Material.getMaterial(id), amount, data);
        }

        if (item == null || item.getType() == Material.AIR) {
            return null;
        }

        MetaItemStack meta = new MetaItemStack(item);
        if (parts.length > 2) {
            meta.parseStringMeta(true, parts, 2);
        }

        return meta;
    }

    public static boolean isInt(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Data
    public static class MetaItemStack {

        private final Pattern splitPattern = Pattern.compile("[:+',;.]");

        private ItemStack stack;
        private String permission;
        private double price;
        private int slot;

        private FireworkEffect.Builder builder = FireworkEffect.builder();
        private PotionEffectType pEffectType;
        private PotionEffect pEffect;
        private boolean validFirework = false;
        private boolean validPotionEffect = false;
        private boolean validPotionDuration = false;
        private boolean validPotionPower = false;
        private boolean completePotion = false;
        private int power = 1;
        private int duration = 120;

        public MetaItemStack(ItemStack stack) {
            this.stack = stack.clone();
            permission = "";
        }

        private void resetPotionMeta() {
            pEffect = null;
            pEffectType = null;
            validPotionEffect = false;
            validPotionDuration = false;
            validPotionPower = false;
            completePotion = true;
        }

        public void parseStringMeta(boolean allowUnsafe, String[] string, int fromArg) {
            if (string[fromArg].startsWith("{")) {
                try {
                    stack = Bukkit.getServer().getUnsafe()
                        .modifyItemStack(stack,
                            Joiner.on(' ').join(Arrays.asList(string).subList(fromArg, string.length)));
                } catch (Exception e) {
                    throw new RuntimeException("invalid item stack");
                }
            } else {
                for (int i = fromArg; i < string.length; i++) {
                    addStringMeta(allowUnsafe, string[i]);
                }
                if (validFirework) {
                    FireworkEffect effect = builder.build();
                    FireworkMeta fmeta = (FireworkMeta) stack.getItemMeta();
                    fmeta.addEffect(effect);
                    if (fmeta.getEffects().size() > 1) {
                        throw new RuntimeException("multiple charges");
                    }
                    stack.setItemMeta(fmeta);
                }
            }
        }

        public void addStringMeta(boolean allowUnsafe, String string) {
            String[] split = splitPattern.split(string, 2);
            if (split.length < 1) {
                return;
            }

            if (split.length > 1 && split[0].equalsIgnoreCase("name")) {
                String displayName = format(split[1].replace('_', ' '));
                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(displayName);
                stack.setItemMeta(meta);
            } else if (split.length > 1 && (split[0].equalsIgnoreCase("lore") || split[0].equalsIgnoreCase("desc"))) {
                List<String> lore = new ArrayList<>();
                for (String line : split[1].split("\\|")) {
                    lore.add(format(line.replace('_', ' ')));
                }
                ItemMeta meta = stack.getItemMeta();
                meta.setLore(lore);
                stack.setItemMeta(meta);
            } else if (split.length > 1 &&
                (split[0].equalsIgnoreCase("player") || split[0].equalsIgnoreCase("owner")) &&
                stack.getType() == Material.SKULL_ITEM) {
                if (stack.getDurability() == 3) {
                    String owner = split[1];
                    SkullMeta meta = (SkullMeta) stack.getItemMeta();
                    meta.setOwner(owner);
                    stack.setItemMeta(meta);
                } else {
                    throw new RuntimeException("only player skulls");
                }
            } else if (split.length > 1 && split[0].equalsIgnoreCase("permission")) {
                permission = split[1];
            } else if (split.length > 1 && (split[0].equalsIgnoreCase("price ") || split[0].equalsIgnoreCase("cost"))) {
                if (isDouble(split[1]))
                    price = Double.parseDouble(split[1]);
                else
                    throw new RuntimeException("bad price syntax, numbers only");
            } else if (split.length > 1 && split[0].equalsIgnoreCase("slot")) {
                if (isInt(split[1]))
                    slot = Integer.parseInt(split[1]);
                else
                    throw new RuntimeException("bad slot syntax, use whole numbers");
            } else if (split.length > 1 && split[0].equalsIgnoreCase("power") && stack.getType() == Material.FIREWORK) {
                int power = isInt(split[1]) ? Integer.parseInt(split[1]) : 0;
                FireworkMeta meta = (FireworkMeta) stack.getItemMeta();
                meta.setPower(power > 3 ? 4 : power);
                stack.setItemMeta(meta);
            } else if (stack.getType() ==
                Material.FIREWORK) //WARNING - Meta for fireworks will be ignored after this point.
            {
                addFireworkMeta(false, string);
            } else if (stack.getType() ==
                Material.POTION) //WARNING - Meta for potions will be ignored after this point.
            {
                addPotionMeta(false, string);
            } else if (split.length > 1 && (split[0].equalsIgnoreCase("color") || split[0].equalsIgnoreCase("colour"))
                && (stack.getType() == Material.LEATHER_BOOTS
                || stack.getType() == Material.LEATHER_CHESTPLATE
                || stack.getType() == Material.LEATHER_HELMET
                || stack.getType() == Material.LEATHER_LEGGINGS)) {
                String[] color = split[1].split("(\\||,)");
                if (color.length == 3) {
                    int red = isInt(color[0]) ? Integer.parseInt(color[0]) : 0;
                    int green = isInt(color[1]) ? Integer.parseInt(color[1]) : 0;
                    int blue = isInt(color[2]) ? Integer.parseInt(color[2]) : 0;
                    LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
                    meta.setColor(Color.fromRGB(red, green, blue));
                    stack.setItemMeta(meta);
                } else {
                    throw new RuntimeException("bad leather syntax");
                }
            } else {
                parseEnchantmentStrings(allowUnsafe, split);
            }
        }

        public void addFireworkMeta(boolean allowShortName, String string) {
            if (stack.getType() == Material.FIREWORK) {
                String[] split = splitPattern.split(string, 2);
                if (split.length < 2) {
                    return;
                }

                if (split[0].equalsIgnoreCase("color") || split[0].equalsIgnoreCase("colour") ||
                    (allowShortName && split[0].equalsIgnoreCase("c"))) {
                    if (validFirework) {
                        FireworkEffect effect = builder.build();
                        FireworkMeta fmeta = (FireworkMeta) stack.getItemMeta();
                        fmeta.addEffect(effect);
                        if (fmeta.getEffects().size() > 1) {
                            throw new RuntimeException("multiple charges");
                        }
                        stack.setItemMeta(fmeta);
                        builder = FireworkEffect.builder();
                    }

                    List<Color> primaryColors = new ArrayList<>();
                    String[] colors = split[1].split(",");
                    for (String color : colors) {
                        if (colorMap.containsKey(color.toUpperCase())) {
                            validFirework = true;
                            primaryColors.add(colorMap.get(color.toUpperCase()).getFireworkColor());
                        } else {
                            throw new RuntimeException("invalid firework format");
                        }
                    }
                    builder.withColor(primaryColors);
                } else if (split[0].equalsIgnoreCase("shape") || split[0].equalsIgnoreCase("type") ||
                    (allowShortName && (split[0].equalsIgnoreCase("s") || split[0].equalsIgnoreCase("t")))) {
                    FireworkEffect.Type finalEffect;
                    split[1] = (split[1].equalsIgnoreCase("large") ? "BALL_LARGE" : split[1]);
                    if (fireworkShape.containsKey(split[1].toUpperCase())) {
                        finalEffect = fireworkShape.get(split[1].toUpperCase());
                    } else {
                        throw new RuntimeException("invalid firework format");
                    }
                    if (finalEffect != null) {
                        builder.with(finalEffect);
                    }
                } else if (split[0].equalsIgnoreCase("fade") || (allowShortName && split[0].equalsIgnoreCase("f"))) {
                    List<Color> fadeColors = new ArrayList<>();
                    String[] colors = split[1].split(",");
                    for (String color : colors) {
                        if (colorMap.containsKey(color.toUpperCase())) {
                            fadeColors.add(colorMap.get(color.toUpperCase()).getFireworkColor());
                        } else {
                            throw new RuntimeException("invalid firework format");
                        }
                    }
                    if (!fadeColors.isEmpty()) {
                        builder.withFade(fadeColors);
                    }
                } else if (split[0].equalsIgnoreCase("effect") || (allowShortName && split[0].equalsIgnoreCase("e"))) {
                    String[] effects = split[1].split(",");
                    for (String effect : effects) {
                        if (effect.equalsIgnoreCase("twinkle")) {
                            builder.flicker(true);
                        } else if (effect.equalsIgnoreCase("trail")) {
                            builder.trail(true);
                        } else {
                            throw new RuntimeException("invalid firework format");
                        }
                    }
                }
            }
        }

        public void addPotionMeta(boolean allowShortName, String string) {
            if (stack.getType() == Material.POTION) {
                String[] split = splitPattern.split(string, 2);
                if (split.length < 2) {
                    return;
                }

                if (split[0].equalsIgnoreCase("effect") || (allowShortName && split[0].equalsIgnoreCase("e"))) {
                    pEffectType = Potions.getByName(split[1]);
                    if (pEffectType != null && pEffectType.getName() != null) {
                        validPotionEffect = true;
                    } else {
                        throw new RuntimeException("invalid potion meta");
                    }
                } else if (split[0].equalsIgnoreCase("power") || (allowShortName && split[0].equalsIgnoreCase("p"))) {
                    if (isInt(split[1])) {
                        validPotionPower = true;
                        power = Integer.parseInt(split[1]);
                        if (power > 0 && power < 4) {
                            power -= 1;
                        }
                    } else {
                        throw new RuntimeException("invalid potion meta");
                    }
                } else if (split[0].equalsIgnoreCase("duration") ||
                    (allowShortName && split[0].equalsIgnoreCase("d"))) {
                    if (isInt(split[1])) {
                        validPotionDuration = true;
                        duration =
                            Integer.parseInt(split[1]) * 20; //Duration is in ticks by default, converted to seconds
                    } else {
                        throw new RuntimeException("invalid potion meta");
                    }
                }

                if (validPotionEffect && validPotionDuration && validPotionPower) {
                    PotionMeta pmeta = (PotionMeta) stack.getItemMeta();
                    pEffect = pEffectType.createEffect(duration, power);
                    if (pmeta.getCustomEffects().size() > 1) {
                        throw new RuntimeException("multiple potion effects");
                    }
                    pmeta.addCustomEffect(pEffect, true);
                    stack.setItemMeta(pmeta);
                    resetPotionMeta();
                }
            }
        }

        private void parseEnchantmentStrings(boolean allowUnsafe, String[] split) {
            Enchantment enchantment = Enchantments.getByName(split[0]);
            if (enchantment == null) {
                throw new RuntimeException("enchantment not found");
            }

            int level = -1;
            if (split.length > 1) {
                try {
                    level = Integer.parseInt(split[1]);
                } catch (NumberFormatException ex) {
                    level = -1;
                }
            }

            if (level < 0 || (!allowUnsafe && level > enchantment.getMaxLevel())) {
                level = enchantment.getMaxLevel();
            }
            addEnchantment(enchantment, allowUnsafe, level);
        }

        public void addEnchantment(Enchantment enchantment, boolean allowUnsafe, int level) {
            if (enchantment == null) {
                throw new RuntimeException("enchantment not found");
            }
            try {
                if (stack.getType().equals(Material.ENCHANTED_BOOK)) {
                    EnchantmentStorageMeta meta = (EnchantmentStorageMeta) stack.getItemMeta();
                    if (level == 0) {
                        meta.removeStoredEnchant(enchantment);
                    } else {
                        meta.addStoredEnchant(enchantment, level, allowUnsafe);
                    }
                    stack.setItemMeta(meta);
                } else // all other material types besides ENCHANTED_BOOK
                {
                    if (level == 0) {
                        stack.removeEnchantment(enchantment);
                    } else {
                        if (allowUnsafe) {
                            stack.addUnsafeEnchantment(enchantment, level);
                        } else {
                            stack.addEnchantment(enchantment, level);
                        }
                    }
                }
            } catch (Exception ex) {
                throw new RuntimeException("Enchantment " + enchantment.getName() + ": " + ex.getMessage(), ex);
            }
        }

        public Enchantment getEnchantment(String name) {
            return Enchantments.getByName(name);
        }

        private String format(String str) {
            return ChatColor.translateAlternateColorCodes('&', str);
        }

    }

}
