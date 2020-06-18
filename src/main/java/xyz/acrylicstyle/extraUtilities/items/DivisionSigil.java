package xyz.acrylicstyle.extraUtilities.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.extraUtilities.item.EUItem;
import xyz.acrylicstyle.extraUtilities.item.AItem;
import xyz.acrylicstyle.paper.Paper;
import xyz.acrylicstyle.paper.inventory.ItemStackUtils;
import xyz.acrylicstyle.paper.nbt.NBTTagCompound;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@AItem
public class DivisionSigil extends EUItem {
    private static final DivisionSigil instance = new DivisionSigil();

    @NotNull
    public static DivisionSigil getInstance() {
        return instance;
    }

    @Override
    public @NotNull ItemStack getItemStack() {
        ItemStack item = addEUTag(new ItemStack(Material.SADDLE));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Division Sigil");
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "INACTIVE: You must perform Activation Ritual.",
                ChatColor.GRAY + "Sneak right-click on an enchanting table",
                ChatColor.GRAY + "for more details"
        ));
        item.setItemMeta(meta);
        ItemStackUtils util = Paper.itemStack(item);
        NBTTagCompound tag = util.getOrCreateTag();
        tag.setBoolean("divisionSigil", true);
        util.setTag(tag);
        return util.getItemStack();
    }

    public ItemStack getActiveItemStack() {
        ItemStack item = getItemStack();
        ItemMeta meta = item.getItemMeta();
        meta.setLore(null);
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(Collections.singletonList(ChatColor.GRAY + "ACTIVE"));
        item.setItemMeta(meta);
        ItemStackUtils util = Paper.itemStack(item);
        NBTTagCompound tag = util.getOrCreateTag();
        tag.setBoolean("divisionSigilActive", true);
        util.setTag(tag);
        return util.getItemStack();
    }

    @Override
    public boolean isCorrectItem(@Nullable ItemStack itemStack) {
        if (itemStack == null) return false;
        NBTTagCompound tag = Paper.itemStack(itemStack).getOrCreateTag();
        return tag.hasKey("divisionSigil") && tag.getBoolean("divisionSigil");
    }

    public boolean isActive(@Nullable ItemStack itemStack) {
        return itemStack != null && Paper.itemStack(itemStack).getOrCreateTag().getBoolean("divisionSigilActive");
    }

    @Override
    public void onBlockRightClick(@NotNull PlayerInteractEvent e) {
        if (!e.getPlayer().isSneaking()) return;
        if (e.getClickedBlock() == null) return;
        if (e.getClickedBlock().getType() != Material.ENCHANTING_TABLE) return;
        if (isActive(e.getItem())) return;
        new Thread(() -> getMessages(e.getClickedBlock()).getKey().forEach(e.getPlayer()::sendMessage)).start();
    }

    @Override
    public void onBlockLeftClick(@NotNull PlayerInteractEvent e) {}

    public Map.Entry<List<String>, Boolean> getMessages(Block block) {
        List<String> messages = new ArrayList<>();
        boolean pass = true;
        messages.add("Activation Ritual");
        if (hasRedStoneCircle(block)) {
            messages.add("- Alter has a redstone circle");
        } else {
            pass = false;
            messages.add("! Alter must have a redstone circle");
        }
        if (isDirt(block)) {
            messages.add("- Alter and Circle placed on dirt");
        } else {
            pass = false;
            messages.add("! Alter and Circle must be placed on the dirt");
        }
        if (canSeeMoon(block)) {
            messages.add("- Alter can see the moon");
        } else {
            pass = false;
            messages.add("! Alter cannot see the moon");
        }
        if (isAlterInDarkness(block)) {
            messages.add("- Alter is in darkness");
        } else {
            pass = false;
            messages.add("! Alter must be in darkness");
        }
        if (isRightTime(block)) {
            messages.add("- Time is right");
        } else {
            pass = false;
            messages.add("! Time isn't right");
        }
        if (pass) messages.add("Perform the sacrifice");
        return new AbstractMap.SimpleImmutableEntry<>(messages, pass);
    }

    private boolean hasRedStoneCircle(Block block) {
        if (block == null) return false;
        World w = block.getWorld();
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        if (w.getBlockAt(x-1, y, z).getType() != Material.REDSTONE_WIRE) return false;
        if (w.getBlockAt(x+1, y, z).getType() != Material.REDSTONE_WIRE) return false;
        if (w.getBlockAt(x, y, z-1).getType() != Material.REDSTONE_WIRE) return false;
        if (w.getBlockAt(x, y, z+1).getType() != Material.REDSTONE_WIRE) return false;
        if (w.getBlockAt(x+1, y, z+1).getType() != Material.REDSTONE_WIRE) return false;
        if (w.getBlockAt(x+1, y, z-1).getType() != Material.REDSTONE_WIRE) return false;
        if (w.getBlockAt(x-1, y, z+1).getType() != Material.REDSTONE_WIRE) return false;
        return w.getBlockAt(x-1, y, z-1).getType() == Material.REDSTONE_WIRE;
    }

    private boolean canSeeMoon(Block block) {
        if (block == null) return false;
        return block.getWorld().getHighestBlockYAt(block.getX(), block.getZ()) == block.getY();
    }

    private boolean isDirt(Block block) {
        if (block == null) return false;
        World w = block.getWorld();
        int x = block.getX();
        int y = block.getY()-1;
        int z = block.getZ();
        if (w.getBlockAt(x, y, z).getType() != Material.DIRT && w.getBlockAt(x, y, z).getType() != Material.GRASS_BLOCK) return false;
        if (w.getBlockAt(x-1, y, z).getType() != Material.DIRT && w.getBlockAt(x-1, y, z).getType() != Material.GRASS_BLOCK) return false;
        if (w.getBlockAt(x+1, y, z).getType() != Material.DIRT && w.getBlockAt(x+1, y, z).getType() != Material.GRASS_BLOCK) return false;
        if (w.getBlockAt(x, y, z-1).getType() != Material.DIRT && w.getBlockAt(x, y, z-1).getType() != Material.GRASS_BLOCK) return false;
        if (w.getBlockAt(x, y, z+1).getType() != Material.DIRT && w.getBlockAt(x, y, z+1).getType() != Material.GRASS_BLOCK) return false;
        if (w.getBlockAt(x+1, y, z+1).getType() != Material.DIRT && w.getBlockAt(x+1, y, z+1).getType() != Material.GRASS_BLOCK) return false;
        if (w.getBlockAt(x+1, y, z-1).getType() != Material.DIRT && w.getBlockAt(x+1, y, z-1).getType() != Material.GRASS_BLOCK) return false;
        if (w.getBlockAt(x-1, y, z+1).getType() != Material.DIRT && w.getBlockAt(x-1, y, z+1).getType() != Material.GRASS_BLOCK) return false;
        return w.getBlockAt(x-1, y, z-1).getType() == Material.DIRT || w.getBlockAt(x-1, y, z-1).getType() == Material.GRASS_BLOCK;
    }

    private boolean isAlterInDarkness(Block block) {
        return block.getLightFromBlocks() < 1;
    }

    private boolean isRightTime(Block block) {
        long time = block.getWorld().getFullTime();
        return time > 17500 && time < 18500;
    }
}
