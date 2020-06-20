package xyz.acrylicstyle.extraUtilities.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.acrylicstyle.paper.Paper;
import xyz.acrylicstyle.paper.inventory.ItemStackUtils;
import xyz.acrylicstyle.paper.nbt.NBTTagCompound;
import xyz.acrylicstyle.tomeito_api.gui.ClickableItem;

import java.util.ArrayList;

public class ItemUtils {
    public static ItemStack getCompressedItemStack(Material material, String displayName) { // copied from CompressedBlocks source code
        ItemStack item = ClickableItem.of(material, 1, ChatColor.YELLOW + displayName, new ArrayList<>(), e -> {}).getItemStack();
        ItemStackUtils util = Paper.itemStack(item);
        NBTTagCompound tag = util.getOrCreateTag();
        tag.setBoolean("compressed", true);
        util.setTag(tag);
        return util.getItemStack();
    }
}
