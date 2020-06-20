package xyz.acrylicstyle.extraUtilities.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.extraUtilities.item.AItem;
import xyz.acrylicstyle.extraUtilities.item.EUItem;
import xyz.acrylicstyle.paper.Paper;
import xyz.acrylicstyle.paper.inventory.ItemStackUtils;
import xyz.acrylicstyle.paper.nbt.NBTTagCompound;

@AItem
public class SemiStableNugget extends EUItem {
    private static final SemiStableNugget instance = new SemiStableNugget();

    public static SemiStableNugget getInstance() { return instance; }

    @Override
    public @NotNull ItemStack getItemStack() {
        ItemStack item = addEUTag(new ItemStack(Material.SADDLE));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Semi-stable Nugget");
        item.setItemMeta(meta);
        ItemStackUtils util = Paper.itemStack(item);
        NBTTagCompound tag = util.getOrCreateTag();
        tag.setBoolean("semi_stable_nugget", true);
        util.setTag(tag);
        return util.getItemStack();
    }

    @Override
    public boolean isCorrectItem(@Nullable ItemStack itemStack) {
        return itemStack != null && Paper.itemStack(itemStack).getOrCreateTag().getBoolean("semi_stable_nugget");
    }
}
