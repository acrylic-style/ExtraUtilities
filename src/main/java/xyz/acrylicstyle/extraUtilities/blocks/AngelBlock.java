package xyz.acrylicstyle.extraUtilities.blocks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.extraUtilities.block.ABlock;
import xyz.acrylicstyle.extraUtilities.block.EUBlock;
import xyz.acrylicstyle.paper.Paper;
import xyz.acrylicstyle.paper.inventory.ItemStackUtils;
import xyz.acrylicstyle.paper.nbt.NBTTagCompound;

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
        meta.setDisplayName(ChatColor.WHITE + "Angel Block");
        item.setItemMeta(meta);
        ItemStackUtils util = Paper.itemStack(item);
        NBTTagCompound tag = util.getOrCreateTag();
        tag.setBoolean("angelBlock", true);
        util.setTag(tag);
        return util.getItemStack();
    }

    @Override
    public boolean isCorrectItem(@Nullable ItemStack itemStack) {
        return itemStack != null && Paper.itemStack(itemStack).getOrCreateTag().getBoolean("angelBlock");
    }
}
