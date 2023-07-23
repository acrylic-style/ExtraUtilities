package xyz.acrylicstyle.extrautilities.items;

import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.extrautilities.ExtraUtilitiesPlugin;
import xyz.acrylicstyle.extrautilities.item.Item;
import xyz.acrylicstyle.extrautilities.item.EUItem;

@Item
public class SemiStableNugget extends EUItem {
    private static final SemiStableNugget instance = new SemiStableNugget();

    public static SemiStableNugget getInstance() { return instance; }

    @Override
    public @NotNull ItemStack getItemStack() {
        ItemStack item = addEUTag(new ItemStack(Material.SADDLE));
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.WHITE + "Semi-stable Nugget");
        meta.setCustomModelData(ExtraUtilitiesPlugin.modelSemiStableNugget);
        item.setItemMeta(meta);
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nms.w(); // getOrCreateTag
        tag.a("semi_stable_nugget", true); // setBoolean
        nms.c(tag); // setTag
        return CraftItemStack.asBukkitCopy(nms);
    }

    @Override
    public boolean isCorrectItem(@Nullable ItemStack itemStack) {
        return itemStack != null && CraftItemStack.asNMSCopy(itemStack).w().q("semi_stable_nugget"); // getOrCreateTag().getBoolean()
    }
}
