package xyz.acrylicstyle.extraUtilities.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.extraUtilities.item.EUItem;
import xyz.acrylicstyle.paper.Paper;
import xyz.acrylicstyle.paper.inventory.ItemStackUtils;
import xyz.acrylicstyle.paper.nbt.NBTBase;
import xyz.acrylicstyle.paper.nbt.NBTTagCompound;

import java.util.Arrays;
import java.util.Collections;

public class UnstableIngot extends EUItem {
    private static final UnstableIngot instance = new UnstableIngot();

    public static UnstableIngot getInstance() { return instance; }

    @Override
    public @NotNull ItemStack getItemStack() {
        ItemStack item = addEUTag(new ItemStack(Material.SADDLE));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Unstable Ingot");
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "ERROR: Divide by diamond",
                ChatColor.GRAY + "This ingot is highly unstable and will explode",
                ChatColor.GRAY + "after 10 seconds.",
                ChatColor.GRAY + "Will also explode if the crafting window is closed",
                ChatColor.GRAY + "or the ingot is thrown on the ground.",
                ChatColor.GRAY + "Additionally these ingots do not stack",
                ChatColor.GRAY + " - Do not craft unless ready -",
                "",
                ChatColor.GRAY + "Must be crafted in a vanilla crafting table."
        ));
        item.setItemMeta(meta);
        ItemStackUtils util = Paper.itemStack(item);
        NBTTagCompound tag = util.getOrCreateTag();
        tag.setBoolean("unstableIngot", true);
        util.setTag(tag);
        return util.getItemStack();
    }

    public ItemStack getTickingItemStack() {
        ItemStackUtils util = Paper.itemStack(getItemStack());
        NBTTagCompound tag = util.getOrCreateTag();
        tag.setBoolean("unstableIngotTicking", true);
        util.setTag(tag);
        return util.getItemStack();
    }

    public ItemStack getStableItemStack() {
        ItemStack item = getItemStack();
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Collections.singletonList(ChatColor.GRAY + "Stable"));
        item.setItemMeta(meta);
        return item;
    }

    public boolean isTicking(ItemStack item) {
        return item != null && Paper.itemStack(item).getOrCreateTag().getBoolean("unstableIngotTicking");
    }

    public int getTicks(ItemStack item) {
        if (!isTicking(item)) return 0;
        NBTBase.NBTNumber number = ((NBTBase.NBTNumber) Paper.itemStack(item).getOrCreateTag().get("unstableIngotTicks"));
        if (number == null) return 0;
        return number.asInt();
    }

    @Nullable
    public ItemStack setTicks(ItemStack item, int ticks) {
        if (!isTicking(item)) return null;
        ItemStackUtils util = Paper.itemStack(item);
        NBTTagCompound tag = util.getOrCreateTag();
        tag.setInt("unstableIngotTicks", ticks);
        util.setTag(tag);
        return util.getItemStack();
    }

    @Override
    public void onBlockRightClick(@NotNull PlayerInteractEvent e) {

    }

    @Override
    public void onBlockLeftClick(@NotNull PlayerInteractEvent e) {

    }

    @Override
    public boolean isCorrectItem(@Nullable ItemStack itemStack) {
        return itemStack != null && Paper.itemStack(itemStack).getOrCreateTag().getBoolean("unstableIngot");
    }
}
