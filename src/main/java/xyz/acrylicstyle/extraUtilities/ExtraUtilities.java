package xyz.acrylicstyle.extraUtilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import util.CollectionList;
import util.ReflectionHelper;
import util.reflect.Ref;
import xyz.acrylicstyle.extraUtilities.item.EUItem;
import xyz.acrylicstyle.extraUtilities.item.Item;
import xyz.acrylicstyle.extraUtilities.items.AngelRing;
import xyz.acrylicstyle.extraUtilities.items.DivisionSigil;
import xyz.acrylicstyle.extraUtilities.items.SemiStableNugget;
import xyz.acrylicstyle.extraUtilities.items.UnstableIngot;
import xyz.acrylicstyle.extraUtilities.util.BlockUtils;
import xyz.acrylicstyle.tomeito_api.utils.Log;

import java.util.Collections;
import java.util.NoSuchElementException;

public class ExtraUtilities extends JavaPlugin implements Listener {
    public static CollectionList<EUItem> classes = new CollectionList<>();
    public static NamespacedKey unstable_ingot;
    public static NamespacedKey semi_stable_nugget;
    public static NamespacedKey semi_stable_nugget_unstable_ingot;

    @Override
    public void onEnable() {
        unstable_ingot = new NamespacedKey(this, "unstable_ingot");
        semi_stable_nugget = new NamespacedKey(this, "semi_stable_nugget");
        semi_stable_nugget_unstable_ingot = new NamespacedKey(this, "semi_stable_nugget_unstable_ingot");
        {
            ShapedRecipe recipe = new ShapedRecipe(unstable_ingot, UnstableIngot.getInstance().getTickingItemStack());
            recipe.shape("I  ", "V  ", "D  ");
            recipe.setIngredient('I', new ItemStack(Material.IRON_INGOT));
            recipe.setIngredient('V', DivisionSigil.getInstance().getActiveItemStack());
            recipe.setIngredient('D', new ItemStack(Material.DIAMOND));
            Bukkit.addRecipe(recipe);
        }
        {
            ShapedRecipe recipe = new ShapedRecipe(semi_stable_nugget, SemiStableNugget.getInstance().getItemStack());
            recipe.shape("N  ", "V  ", "D  ");
            recipe.setIngredient('N', new ItemStack(Material.GOLD_NUGGET));
            recipe.setIngredient('V', DivisionSigil.getInstance().getActiveItemStack());
            recipe.setIngredient('D', new ItemStack(Material.DIAMOND));
            Bukkit.addRecipe(recipe);
        }
        {
            ShapedRecipe recipe = new ShapedRecipe(semi_stable_nugget_unstable_ingot, UnstableIngot.getInstance().getStableItemStack());
            recipe.shape("NNN", "NNN", "NNN");
            recipe.setIngredient('N', SemiStableNugget.getInstance().getItemStack());
            Bukkit.addRecipe(recipe);
        }
        Bukkit.getPluginManager().registerEvents(this, this);
        classes.clear();
        classes.addAll(ReflectionHelper.findAllAnnotatedClasses(this.getClassLoader(), "xyz.acrylicstyle.extraUtilities.items", Item.class)
                .filter(clazz -> clazz.getSuperclass().equals(EUItem.class))
                .map(clazz -> {
                    Log.info("Registering item " + clazz.getName());
                    return (EUItem) Ref.getMethodOptional(clazz, "getInstance")
                            .orElseThrow(() -> new NoSuchElementException("Requires getInstance method"))
                            .invoke(null);
                }));
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    ItemStack[] c = player.getInventory().getContents();
                    long time = player.getWorld().getFullTime();
                    if (time > 17500 && time < 18500) {
                        new Thread(() -> {
                            for (ItemStack itemStack : c) {
                                if (DivisionSigil.getInstance().isCorrectItem(itemStack)) {
                                    ItemMeta meta = itemStack.getItemMeta();
                                    meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
                                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
                                    itemStack.setItemMeta(meta);
                                }
                            }
                        }).start();
                    } else {
                        new Thread(() -> {
                            for (ItemStack itemStack : c) {
                                if (DivisionSigil.getInstance().isCorrectItem(itemStack) && !DivisionSigil.getInstance().isActive(itemStack)) {
                                    ItemMeta meta = itemStack.getItemMeta();
                                    meta.removeEnchant(Enchantment.PROTECTION_ENVIRONMENTAL);
                                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
                                    itemStack.setItemMeta(meta);
                                }
                            }
                        }).start();
                    }
                    if (player.getGameMode() == GameMode.SURVIVAL) {
                        new Thread(() -> {
                            for (ItemStack item : c) {
                                if (AngelRing.getInstance().isCorrectItem(item)) {
                                    player.setAllowFlight(true);
                                    return;
                                }
                            }
                            player.setAllowFlight(false);
                        }).start();
                    }
                });
            }
        }.runTaskTimer(this, 20, 20);
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    Inventory inventory = player.getInventory();
                    ItemStack[] c = inventory.getContents();
                    for (int i = 0; i < c.length; i++) {
                        tickDivisionSigil(i, inventory, player, c[i]);
                    }
                    if (player.getOpenInventory().getTopInventory() instanceof CraftingInventory) {
                        inventory = player.getOpenInventory().getTopInventory();
                        c = ((CraftingInventory) inventory).getMatrix();
                        for (int i = 0; i < c.length; i++) {
                            tickDivisionSigil(i + 1, inventory, player, c[i]);
                        }
                    }
                });
            }
        }.runTaskTimer(this, 2, 2);
    }

    private void tickDivisionSigil(int i, Inventory inventory, Player player, ItemStack item) {
        if (UnstableIngot.getInstance().isTicking(item)) {
            int ticks = UnstableIngot.getInstance().getTicks(item);
            if (ticks >= 100) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        inventory.setItem(i, null);
                        player.getWorld().createExplosion(player, player.getLocation(), 7, true);
                    }
                }.runTask(ExtraUtilities.this);
                return;
            }
            item = UnstableIngot.getInstance().setTicks(item, ticks + 1);
            assert item != null;
            ItemMeta meta = item.getItemMeta();
            meta.setLore(Collections.singletonList(ChatColor.GRAY + "Exploding in " + ((100 - ticks) / 10D) + " seconds"));
            item.setItemMeta(meta);
            ItemStack finalItem = item;
            if (inventory.getItem(i) != null) inventory.setItem(i, finalItem);
        }
    }

    @Override
    public void onDisable() {
        Bukkit.removeRecipe(unstable_ingot);
        Bukkit.removeRecipe(semi_stable_nugget);
        Bukkit.removeRecipe(semi_stable_nugget_unstable_ingot);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        PlayerInventory i = e.getPlayer().getInventory();
        if (EUItem.isEUItem(i.getItemInMainHand()) || EUItem.isEUItem(i.getItemInOffHand())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if (UnstableIngot.getInstance().isTicking(e.getItemDrop().getItemStack())) {
            e.getItemDrop().remove();
            e.getPlayer().getWorld().createExplosion(e.getPlayer(), e.getItemDrop().getLocation(), 7, true);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity().getKiller() != null) { // division sigil activation
            Block block = BlockUtils.getNearbyBlocks(e.getEntity().getLocation(), 3, Collections.singletonList(Material.ENCHANTING_TABLE)).first();
            if (block != null) {
                if (block.getLocation().distance(e.getEntity().getLocation()) < 1.8) {
                    if (DivisionSigil.getInstance().getMessages(block).getValue()) {
                        block.getWorld().strikeLightningEffect(block.getLocation());
                        block.getWorld().playSound(block.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.2F, 1);
                        block.getWorld().playSound(block.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.2F, 0.5F);
                        block.getWorld().playSound(block.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.2F, 1);
                        new Thread(() -> {
                            ItemStack[] c = e.getEntity().getKiller().getInventory().getContents();
                            for (int i = 0; i < c.length; i++) {
                                ItemStack item = c[i];
                                if (DivisionSigil.getInstance().isCorrectItem(item) && !DivisionSigil.getInstance().isActive(item)) {
                                    e.getEntity().getKiller().getInventory().setItem(i, DivisionSigil.getInstance().getActiveItemStack());
                                }
                            }
                        }).start();
                    }
                }
            }
        }
        if (e.getEntity().getType() == EntityType.WITHER) {
            if (Math.random() < 0.5) {
                e.getDrops().add(DivisionSigil.getInstance().getItemStack()); // drops (inactive) division sigil with the chances of 50%
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getType() != InventoryType.WORKBENCH) return;
        ItemStack[] c = e.getPlayer().getInventory().getContents();
        for (int i = 0; i < c.length; i++) {
            ItemStack item = c[i];
            if (UnstableIngot.getInstance().isTicking(item)) {
                e.getPlayer().getInventory().setItem(i, null);
                e.getPlayer().getWorld().createExplosion(e.getPlayer(), e.getPlayer().getLocation(), 7, true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            classes.filter(eu -> eu.isCorrectItem(e.getItem())).forEach(eu -> eu.onBlockRightClick(e));
        }
        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            classes.filter(eu -> eu.isCorrectItem(e.getItem())).forEach(eu -> eu.onBlockLeftClick(e));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getType() != InventoryType.WORKBENCH) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                CraftingInventory inventory = (CraftingInventory) e.getInventory();
                ItemStack[] matrix = inventory.getMatrix();
                {
                    if (matrix[0] != null && matrix[0].getType() == Material.GLASS
                            && matrix[1] != null && matrix[1].getType() == Material.GOLD_INGOT
                            && matrix[2] != null && matrix[2].getType() == Material.GLASS
                            && matrix[3] != null && matrix[3].getType() == Material.GOLD_INGOT
                            && matrix[4] != null && matrix[4].isSimilar(new ItemStack(Material.NETHER_STAR))
                            && matrix[5] != null && matrix[5].getType() == Material.GOLD_INGOT
                            && matrix[6] != null && UnstableIngot.getInstance().isCorrectItem(matrix[6])
                            && matrix[7] != null && matrix[7].getType() == Material.GOLD_INGOT
                            && matrix[8] != null && UnstableIngot.getInstance().isCorrectItem(matrix[8])) {
                        inventory.setResult(AngelRing.getInstance().getItemStack());
                    }
                }
                ((Player) e.getWhoClicked()).updateInventory();
            }
        }.runTaskLater(this, 1); // we need delay!
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent e) {
        if (!UnstableIngot.getInstance().isCorrectItem(e.getInventory().getResult())
                && !SemiStableNugget.getInstance().isCorrectItem(e.getInventory().getResult())) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (UnstableIngot.getInstance().isCorrectItem(e.getInventory().getResult())
                        && !UnstableIngot.getInstance().isTicking(e.getInventory().getResult())) return;
                e.getInventory().setItem(4, DivisionSigil.getInstance().getActiveItemStack());
                if (e.getPlayer() != null) e.getPlayer().updateInventory();
            }
        }.runTask(this);
    }
}
