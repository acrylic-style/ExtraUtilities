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
import xyz.acrylicstyle.extrautilities.item.EUItem;
import xyz.acrylicstyle.extrautilities.item.Item;

@Item
public class AngelRing extends EUItem {
    private static final AngelRing instance = new AngelRing();

    public static AngelRing getInstance() { return instance; }

    @Override
    public @NotNull ItemStack getItemStack() {
        ItemStack item = addEUTag(new ItemStack(Material.SADDLE));
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Angel Ring");
        meta.setCustomModelData(ExtraUtilitiesPlugin.modelAngelRing);
        item.setItemMeta(meta);
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nms.w(); // getOrCreateTag()
        tag.a("angelRing", true); // getBoolean
        nms.c(tag); // setTag
        return CraftItemStack.asBukkitCopy(nms);
    }

    @Override
    public boolean isCorrectItem(@Nullable ItemStack itemStack) {
        return itemStack != null && CraftItemStack.asNMSCopy(itemStack).w().q("angelRing"); // getOrCreateTag().getBoolean()
    }
}
