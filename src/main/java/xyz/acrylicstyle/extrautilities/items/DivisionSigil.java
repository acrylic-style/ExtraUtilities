package xyz.acrylicstyle.extrautilities.items;

import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.extrautilities.ExtraUtilitiesPlugin;
import xyz.acrylicstyle.extrautilities.item.EUItem;
import xyz.acrylicstyle.extrautilities.item.Item;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Item
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
        assert meta != null;
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Division Sigil");
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "INACTIVE: You must perform Activation Ritual.",
                ChatColor.GRAY + "Sneak right-click on an enchanting table",
                ChatColor.GRAY + "for more details"
        ));
        meta.setCustomModelData(ExtraUtilitiesPlugin.modelDivisionSigil);
        item.setItemMeta(meta);
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nms.w(); // getOrCreateTag
        tag.a("divisionSigil", true); // setBoolean
        nms.c(tag); // setTag
        return CraftItemStack.asBukkitCopy(nms);
    }

    public ItemStack getActiveItemStack() {
        ItemStack item = getItemStack();
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setLore(null);
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(Collections.singletonList(ChatColor.GRAY + "ACTIVE"));
        item.setItemMeta(meta);
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nms.w(); // getOrCreateTag
        tag.a("divisionSigilActive", true); // setBoolean
        nms.c(tag); // setTag
        return CraftItemStack.asBukkitCopy(nms);
    }

    @Override
    public boolean isCorrectItem(@Nullable ItemStack itemStack) {
        if (itemStack == null) return false;
        NBTTagCompound tag = CraftItemStack.asNMSCopy(itemStack).w(); // getOrCreateTag
        return tag.e("divisionSigil") /* hasKey */ && tag.q("divisionSigil"); /* getBoolaen */
    }

    public boolean isActive(@Nullable ItemStack itemStack) {
        return itemStack != null && CraftItemStack.asNMSCopy(itemStack).w().q("divisionSigilActive"); // getOrCreateTag().getBoolean()
    }

    @Override
    public void onBlockRightClick(@NotNull PlayerInteractEvent e) {
        if (!e.getPlayer().isSneaking()) return;
        if (e.getClickedBlock() == null) return;
        if (e.getClickedBlock().getType() != Material.ENCHANTING_TABLE) return;
        if (isActive(e.getItem())) return;
        e.setUseItemInHand(Event.Result.ALLOW);
        e.setUseInteractedBlock(Event.Result.ALLOW);
        getMessages(e.getClickedBlock()).getKey().forEach(e.getPlayer()::sendMessage);
    }

    public Map.Entry<List<String>, Boolean> getMessages(Block block) {
        List<String> messages = new ArrayList<>();
        boolean pass = true;
        messages.add("Activation Ritual");
        if (hasRedStoneCircle(block)) {
            messages.add("- 台の周りにレッドストーンがあります"); // (Alter has a redstone circle)");
        } else {
            pass = false;
            messages.add("! 台の周りにレッドストーンが必要です"); // (Alter must have a redstone circle)");
        }
        if (isDirt(block)) {
            messages.add("- 台とレッドストーンが土の上にあります "); // (Alter and Circle placed on dirt)");
        } else {
            pass = false;
            messages.add("! 台とレッドストーンは土の上に設置する必要があります"); // (Alter and Circle must be placed on the dirt)");
        }
        if (canSeeMoon(block)) {
            messages.add("- 台は月が見える状態です"); // (Alter can see the moon)");
        } else {
            pass = false;
            messages.add("! 台は月が見える状態にする必要があります（ガラスは使えません）"); // (Alter cannot see the moon");
        }
        if (isAlterInDarkness(block)) {
            messages.add("- 台の周りは真っ暗です"); // (Alter is in darkness");
        } else {
            pass = false;
            messages.add("! 台の周りは真っ暗にする必要があります(光源レベル7以下)"); // (Alter must be in darkness");
        }
        TimeReason time = isRightTime(block);
        if (time == TimeReason.RIGHT) {
            messages.add("- 現在このワールドは夜です");
        } else if (time == TimeReason.NOT_RIGHT) {
            pass = false;
            messages.add("! 夜間のみ儀式ができます");
        } else if (time == TimeReason.LOCKED) {
            pass = false;
            messages.add("! doDaylightCycleはオフです");
        }
        if (pass) messages.add("儀式は実行可能です");
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
        return block.getLightFromBlocks() <= 7;
    }

    private TimeReason isRightTime(Block block) {
        if (!Objects.requireNonNull(block.getWorld().getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE))) return TimeReason.LOCKED;
        long time = block.getWorld().getTime();
        return time > 17500 && time < 18500 ? TimeReason.RIGHT : TimeReason.NOT_RIGHT;
    }

    public enum TimeReason {
        RIGHT,
        NOT_RIGHT,
        LOCKED
    }
}
