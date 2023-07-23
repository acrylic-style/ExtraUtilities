package xyz.acrylicstyle.extrautilities.items;

import net.minecraft.nbt.NBTNumber;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.extrautilities.ExtraUtilitiesPlugin;
import xyz.acrylicstyle.extrautilities.item.EUItem;

import java.util.Arrays;
import java.util.Collections;

public class UnstableIngot extends EUItem {
    private static final UnstableIngot instance = new UnstableIngot();

    public static UnstableIngot getInstance() { return instance; }

    @Override
    public @NotNull ItemStack getItemStack() {
        ItemStack item = addEUTag(new ItemStack(Material.SADDLE));
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
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
        meta.setCustomModelData(ExtraUtilitiesPlugin.modelUnstableIngot);
        item.setItemMeta(meta);
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nms.w(); // getOrCreateTag
        tag.a("unstableIngot", true); // setBoolean
        nms.c(tag); // setTag
        return CraftItemStack.asBukkitCopy(nms);
    }

    public ItemStack getTickingItemStack() {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(getItemStack());
        NBTTagCompound tag = nms.w(); // getOrCreateTag()
        tag.a("unstableIngotTicking", true); // setBoolean
        nms.c(tag); // setTag
        return CraftItemStack.asBukkitCopy(nms);
    }

    public ItemStack getStableItemStack() {
        ItemStack item = getItemStack();
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setLore(Collections.singletonList(ChatColor.GRAY + "Stable"));
        item.setItemMeta(meta);
        return item;
    }

    public boolean isTicking(ItemStack item) {
        return item != null && CraftItemStack.asNMSCopy(item).w().q("unstableIngotTicking"); // getOrCreateTag().getBoolean()
    }

    public int getTicks(ItemStack item) {
        if (!isTicking(item)) return 0;
        NBTNumber number = ((NBTNumber) CraftItemStack.asNMSCopy(item).w().c("unstableIngotTicks")); // getOrCreateTag().get()
        if (number == null) return 0;
        return number.g(); // getAsInt
    }

    @Nullable
    public ItemStack setTicks(ItemStack item, int ticks) {
        if (!isTicking(item)) return null;
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nms.w(); // getOrCreateTag
        tag.a("unstableIngotTicks", ticks); // setBoolean
        nms.c(tag); // setTag
        return CraftItemStack.asBukkitCopy(nms);
    }

    @Override
    public boolean isCorrectItem(@Nullable ItemStack itemStack) {
        return itemStack != null && CraftItemStack.asNMSCopy(itemStack).w().q("unstableIngot"); // getOrCreateTag().getBoolean()
    }
}
