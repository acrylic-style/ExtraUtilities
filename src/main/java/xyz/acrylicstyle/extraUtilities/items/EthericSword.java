package xyz.acrylicstyle.extraUtilities.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.extraUtilities.item.EUItem;
import xyz.acrylicstyle.paper.Paper;
import xyz.acrylicstyle.paper.inventory.ItemStackUtils;
import xyz.acrylicstyle.paper.nbt.NBTTagCompound;

import java.util.Arrays;

public class EthericSword extends EUItem {
    private static final EthericSword instance = new EthericSword();

    public static EthericSword getInstance() { return instance; }

    @Override
    public @NotNull ItemStack getItemStack() {
        ItemStack item = addEUTag(new ItemStack(Material.DIAMOND_SWORD));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "Etheric Sword");
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("Attack Damage", 6.5, AttributeModifier.Operation.ADD_NUMBER));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(Arrays.asList(
                ChatColor.BLUE + "+10% True Damage",
                ChatColor.BLUE + "+8.5 Attack Damage",
                ChatColor.BLUE + "+10% Durability"
        ));
        item.setItemMeta(meta);
        ItemStackUtils util = Paper.itemStack(item);
        NBTTagCompound tag = util.getOrCreateTag();
        tag.setBoolean("ethericSword", true);
        util.setTag(tag);
        return util.getItemStack();
    }

    @Override
    public boolean isCorrectItem(@Nullable ItemStack itemStack) {
        return itemStack != null && Paper.itemStack(itemStack).getOrCreateTag().getBoolean("ethericSword");
    }
}
