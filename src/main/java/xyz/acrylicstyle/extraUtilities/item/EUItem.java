package xyz.acrylicstyle.extraUtilities.item;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.paper.Paper;
import xyz.acrylicstyle.paper.inventory.ItemStackUtils;
import xyz.acrylicstyle.paper.nbt.NBTTagCompound;

public abstract class EUItem {
    @NotNull
    public abstract ItemStack getItemStack();

    public void onBlockRightClick(@NotNull PlayerInteractEvent e) {}

    public void onBlockLeftClick(@NotNull PlayerInteractEvent e) {}

    public abstract boolean isCorrectItem(@Nullable ItemStack itemStack);

    @NotNull
    protected final ItemStack addEUTag(@NotNull ItemStack item) {
        ItemStackUtils util = Paper.itemStack(item);
        NBTTagCompound tag = util.getOrCreateTag();
        tag.setBoolean("extraUtilityItem", true);
        util.setTag(tag);
        return util.getItemStack();
    }

    public static boolean isEUItem(@Nullable ItemStack item) {
        return item != null && Paper.itemStack(item).getOrCreateTag().getBoolean("extraUtilityItem");
    }
}
