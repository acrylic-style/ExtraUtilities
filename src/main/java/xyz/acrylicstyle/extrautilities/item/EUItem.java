package xyz.acrylicstyle.extrautilities.item;

import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class EUItem {
    @NotNull
    public abstract ItemStack getItemStack();

    public void onBlockRightClick(@NotNull PlayerInteractEvent e) {}

    public void onBlockLeftClick(@NotNull PlayerInteractEvent e) {}

    public abstract boolean isCorrectItem(@Nullable ItemStack itemStack);

    @NotNull
    protected final ItemStack addEUTag(@NotNull ItemStack item) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nms.w(); // getOrCreateTag()
        tag.a("extraUtilityItem", true); // setBoolean
        nms.c(tag); // setTag
        return CraftItemStack.asBukkitCopy(nms);
    }

    public static boolean isEUItem(@Nullable ItemStack item) {
        return item != null && CraftItemStack.asNMSCopy(item).w().q("extraUtilityItem"); // getOrCreateTag().getBoolean()
    }
}
