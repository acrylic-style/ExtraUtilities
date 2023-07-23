package xyz.acrylicstyle.extrautilities.items;

import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.extrautilities.ExtraUtilitiesPlugin;
import xyz.acrylicstyle.extrautilities.item.EUItem;

import java.util.Arrays;

public class EthericSword extends EUItem {
    private static final EthericSword instance = new EthericSword();

    public static EthericSword getInstance() { return instance; }

    @Override
    public @NotNull ItemStack getItemStack() {
        ItemStack item = addEUTag(new ItemStack(Material.NETHERITE_SWORD));
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.RESET + "Etheric Sword");
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("Attack Damage", 6.5, AttributeModifier.Operation.ADD_NUMBER));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(Arrays.asList(
                ChatColor.BLUE + "+10% True Damage",
                ChatColor.BLUE + "+8.5 Attack Damage",
                ChatColor.BLUE + "+10% Durability"
        ));
        meta.setCustomModelData(ExtraUtilitiesPlugin.modelEthericSword);
        item.setItemMeta(meta);
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nms.w(); // getOrCreateTag
        tag.a("ethericSword", true); // setBoolean
        nms.c(tag); // setTag
        return CraftItemStack.asBukkitCopy(nms);
    }

    @Override
    public boolean isCorrectItem(@Nullable ItemStack itemStack) {
        return itemStack != null && CraftItemStack.asNMSCopy(itemStack).w().q("ethericSword"); // getOrCreateTag().getBoolean()
    }
}
