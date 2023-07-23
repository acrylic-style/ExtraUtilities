package xyz.acrylicstyle.extrautilities.blocks;

import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.extrautilities.block.ABlock;
import xyz.acrylicstyle.extrautilities.block.EUBlock;

@ABlock
public class AngelBlock extends EUBlock {
    private static final AngelBlock instance = new AngelBlock();

    public static AngelBlock getInstance() { return instance; }

    @Override
    public @NotNull Material getType() {
        return Material.JIGSAW;
    }

    @Override
    public boolean isRightBlock(@NotNull Block block) {
        return block.getType() == Material.JIGSAW;
    }

    @Override
    public @NotNull ItemStack getItemStack() {
        ItemStack item = new ItemStack(Material.JIGSAW);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.WHITE + "Angel Block");
        item.setItemMeta(meta);
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nms.w(); // getOrCreateTag()
        tag.a("angelBlock", true); // setBoolean
        nms.c(tag); // setTag
        return CraftItemStack.asBukkitCopy(nms);
    }

    @Override
    public boolean isCorrectItem(@Nullable ItemStack itemStack) {
        return itemStack != null && CraftItemStack.asNMSCopy(itemStack).w().q("angelBlock"); // getOrCreateTag().getBoolean()
    }
}
