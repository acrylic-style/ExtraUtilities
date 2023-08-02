package xyz.acrylicstyle.extrautilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.extrautilities.blocks.AngelBlock;
import xyz.acrylicstyle.extrautilities.event.PlayerAngelRingEvent;
import xyz.acrylicstyle.extrautilities.event.PlayerAngelRingToggleFlightEvent;
import xyz.acrylicstyle.extrautilities.item.EUItem;
import xyz.acrylicstyle.extrautilities.items.AngelRing;
import xyz.acrylicstyle.extrautilities.items.DivisionSigil;
import xyz.acrylicstyle.extrautilities.items.EthericSword;
import xyz.acrylicstyle.extrautilities.items.SemiStableNugget;
import xyz.acrylicstyle.extrautilities.items.UnstableIngot;
import xyz.acrylicstyle.extrautilities.util.BlockUtils;

import java.util.*;

public class ExtraUtilitiesPlugin extends JavaPlugin implements Listener {
    public static final List<EUItem> ITEMS = new ArrayList<>();
    public static NamespacedKey unstable_ingot;
    public static NamespacedKey semi_stable_nugget;
    public static NamespacedKey semi_stable_nugget_unstable_ingot;
    public static NamespacedKey angel_block;
    public static ExtraUtilitiesPlugin instance;
    public static int modelAngelRing = 0;
    public static int modelDivisionSigil = 0;
    public static int modelEthericSword = 0;
    public static int modelSemiStableNugget = 0;
    public static int modelUnstableIngot = 0;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        modelAngelRing = getConfig().getInt("custom-model-data.angel-ring", 1);
        modelDivisionSigil = getConfig().getInt("custom-model-data.division-sigil", 2);
        modelEthericSword = getConfig().getInt("custom-model-data.etheric-sword", 1);
        modelSemiStableNugget = getConfig().getInt("custom-model-data.semi-stable-nugget", 3);
        modelUnstableIngot = getConfig().getInt("custom-model-data.unstable-ingot", 4);
        unstable_ingot = new NamespacedKey(this, "unstable_ingot");
        semi_stable_nugget = new NamespacedKey(this, "semi_stable_nugget");
        semi_stable_nugget_unstable_ingot = new NamespacedKey(this, "semi_stable_nugget_unstable_ingot");
        angel_block = new NamespacedKey(this, "angel_block");
        {
            ShapedRecipe recipe = new ShapedRecipe(unstable_ingot, UnstableIngot.getInstance().getTickingItemStack());
            recipe.shape("I  ", "V  ", "D  ");
            recipe.setIngredient('I', Material.IRON_INGOT);
            recipe.setIngredient('V', new RecipeChoice.ExactChoice(DivisionSigil.getInstance().getActiveItemStack()));
            recipe.setIngredient('D', Material.DIAMOND);
            Bukkit.addRecipe(recipe);
        }
        {
            ShapedRecipe recipe = new ShapedRecipe(semi_stable_nugget, SemiStableNugget.getInstance().getItemStack());
            recipe.shape("N  ", "V  ", "D  ");
            recipe.setIngredient('N', Material.GOLD_NUGGET);
            recipe.setIngredient('V', new RecipeChoice.ExactChoice(DivisionSigil.getInstance().getActiveItemStack()));
            recipe.setIngredient('D', Material.DIAMOND);
            Bukkit.addRecipe(recipe);
        }
        {
            ShapedRecipe recipe = new ShapedRecipe(semi_stable_nugget_unstable_ingot, UnstableIngot.getInstance().getStableItemStack());
            recipe.shape("NNN", "NNN", "NNN");
            recipe.setIngredient('N', new RecipeChoice.ExactChoice(SemiStableNugget.getInstance().getItemStack()));
            Bukkit.addRecipe(recipe);
        }
        {
            ShapedRecipe recipe = new ShapedRecipe(angel_block, AngelBlock.getInstance().getItemStack());
            recipe.shape(" G ", "FOF", "   ");
            recipe.setIngredient('G', Material.GOLD_INGOT);
            recipe.setIngredient('F', Material.FEATHER);
            recipe.setIngredient('O', Material.OBSIDIAN);
            Bukkit.addRecipe(recipe);
        }
        // todo: soul fragment with shapeless recipe?
        Bukkit.getPluginManager().registerEvents(this, this);
        ITEMS.add(new AngelRing());
        ITEMS.add(new DivisionSigil());
        ITEMS.add(new EthericSword());
        ITEMS.add(new SemiStableNugget());
        ITEMS.add(new UnstableIngot());
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    ItemStack[] c = player.getInventory().getContents();
                    long time = player.getWorld().getTime();
                    if (time > 17500 && time < 18500) {
                        for (ItemStack itemStack : c) {
                            if (DivisionSigil.getInstance().isCorrectItem(itemStack)) {
                                ItemMeta meta = itemStack.getItemMeta();
                                assert meta != null;
                                meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
                                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
                                itemStack.setItemMeta(meta);
                            }
                        }
                    } else {
                        for (ItemStack itemStack : c) {
                            if (DivisionSigil.getInstance().isCorrectItem(itemStack) && !DivisionSigil.getInstance().isActive(itemStack)) {
                                ItemMeta meta = itemStack.getItemMeta();
                                assert meta != null;
                                meta.removeEnchant(Enchantment.PROTECTION_ENVIRONMENTAL);
                                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
                                itemStack.setItemMeta(meta);
                            }
                        }
                    }
                    if (player.getGameMode() == GameMode.SURVIVAL) {
                        if (hasAngelRing(player)) {
                            if (!player.getAllowFlight()) {
                                player.setAllowFlight(true);
                                /*
                                PlayerAngelRingEvent event = new PlayerAngelRingEvent(player, PlayerAngelRingEvent.State.ENABLED);
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCancelled()) {
                                }
                                */
                            }
                        } else {
                            if (player.getAllowFlight()) {
                                player.setAllowFlight(false);
                                /*
                                PlayerAngelRingEvent event = new PlayerAngelRingEvent(player, PlayerAngelRingEvent.State.DISABLED);
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCancelled()) {
                                }
                                */
                            }
                        }
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
                    } else if (!(player.getOpenInventory().getTopInventory() instanceof PlayerInventory)) {
                        inventory = player.getOpenInventory().getTopInventory();
                        c = inventory.getContents();
                        for (int i = 0; i < c.length; i++) {
                            tickDivisionSigil(i, inventory, player, c[i]);
                        }
                    }
                });
            }
        }.runTaskTimer(this, 2, 2);
    }

    @Contract("null -> false")
    public static boolean hasAngelRing(@Nullable Player player) {
        if (player == null) return false;
        ItemStack[] c = player.getInventory().getContents();
        AngelRing i = AngelRing.getInstance();
        for (ItemStack itemStack : c) {
            if (i.isCorrectItem(itemStack)) return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent e) {
        if (e instanceof PlayerAngelRingToggleFlightEvent) return;
        if (e.getPlayer().getGameMode() != GameMode.SURVIVAL) return;
        if (hasAngelRing(e.getPlayer())) {
            PlayerAngelRingToggleFlightEvent event = new PlayerAngelRingToggleFlightEvent(e.getPlayer(), e.isFlying(), e.isFlying() ? PlayerAngelRingEvent.State.ENABLED : PlayerAngelRingEvent.State.DISABLED);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                e.setCancelled(true);
            }
        }
    }

    private void tickDivisionSigil(int i, Inventory inventory, Player player, ItemStack item) {
        if (UnstableIngot.getInstance().isTicking(item)) {
            int ticks = UnstableIngot.getInstance().getTicks(item);
            if (ticks >= 100) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        inventory.setItem(i, null);
                        player.getWorld().createExplosion(player.getLocation(), 7, true);
                    }
                }.runTask(ExtraUtilitiesPlugin.this);
                return;
            }
            item = UnstableIngot.getInstance().setTicks(item, ticks + 1);
            assert item != null;
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
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
        Bukkit.removeRecipe(angel_block);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        PlayerInventory i = e.getPlayer().getInventory();
        if (EUItem.isEUItem(i.getItemInMainHand()) || EUItem.isEUItem(i.getItemInOffHand())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByPlayer(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player damager)) return;
        if (!(e.getEntity() instanceof LivingEntity)) return;
        if (EthericSword.getInstance().isCorrectItem(damager.getInventory().getItemInMainHand())) {
            ((LivingEntity) e.getEntity()).damage(e.getDamage()*0.1); // 10% of the damage is true damage
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if (UnstableIngot.getInstance().isTicking(e.getItemDrop().getItemStack())) {
            e.getItemDrop().remove();
            e.getPlayer().getWorld().createExplosion(e.getItemDrop().getLocation(), 7, true);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity().getKiller() != null) { // division sigil activation
            Block block = BlockUtils.getNearbyBlocks(e.getEntity().getLocation(), 3, Collections.singletonList(Material.ENCHANTING_TABLE)).stream().findFirst().orElse(null);
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
            if (Math.random() < 0.1) { // when in multiplayer, it's very easy to get division sigil, so we'll limit drop chance to 10% here
                e.getDrops().add(DivisionSigil.getInstance().getItemStack());
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
                e.getPlayer().getWorld().createExplosion(e.getPlayer().getLocation(), 7, true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        boolean mainHand = e.getHand() == EquipmentSlot.HAND;
        PlayerInventory inv = e.getPlayer().getInventory();
        if (AngelBlock.getInstance().isCorrectItem(mainHand ? inv.getItemInMainHand() : inv.getItemInOffHand()) && e.getAction().name().startsWith("RIGHT_CLICK_")) {
            Location loc = e.getPlayer().getLocation();
            Vector vector = loc.getDirection();
            double x = vector.getX();
            double y = vector.getY();
            double z = vector.getZ();
            double x1 = loc.getX();
            double y1 = loc.getY();
            double z1 = loc.getZ();
            Block block = new Location(loc.getWorld(), x1 + x, y1 + y, z1 + z).getBlock();
            if (block.getType() != Material.AIR) return;
            block.setType(Material.JIGSAW);
            if (mainHand) {
                inv.getItemInMainHand().setAmount(inv.getItemInMainHand().getAmount()-1);
            } else {
                inv.getItemInOffHand().setAmount(inv.getItemInOffHand().getAmount()-1);
            }
            return; // todo: remove "return"?
        }
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ITEMS.stream().filter(eu -> eu.isCorrectItem(e.getItem())).forEach(eu -> eu.onBlockRightClick(e));
        }
        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.JIGSAW) {
                e.getPlayer().getInventory().addItem(AngelBlock.getInstance().getItemStack());
                Objects.requireNonNull(e.getClickedBlock()).setType(Material.AIR);
                return;
            }
            ITEMS.stream().filter(eu -> eu.isCorrectItem(e.getItem())).forEach(eu -> eu.onBlockLeftClick(e));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getType() == InventoryType.CHEST) {
            if (UnstableIngot.getInstance().isCorrectItem(e.getCurrentItem())) {
                e.setCancelled(true);
            }
        }
        checkRecipe((Player) e.getWhoClicked(), e.getSlot(), e.getInventory(), e);
    }

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent e) {
        if (EthericSword.getInstance().isCorrectItem(e.getItem())) {
            if (Math.random() < 0.10) e.setCancelled(true);
        }
    }

    public static void checkRecipe(@NotNull Player player, int slot, @NotNull Inventory inventory, @Nullable Cancellable cancellable) {
        if (inventory.getType() != InventoryType.WORKBENCH) return;
        boolean isResultSlot = slot == 0;
        new BukkitRunnable() {
            @Override
            public void run() {
                CraftingInventory ci = (CraftingInventory) inventory;
                ItemStack[] matrix = ci.getMatrix();
                // Angel Ring
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
                        if (isResultSlot) {
                            ci.setMatrix(getItemStacks(ci.getMatrix()));
                            player.getInventory().addItem(AngelRing.getInstance().getItemStack());
                        } else {
                            ci.setResult(AngelRing.getInstance().getItemStack());
                        }
                    }
                }
                // Etheric Sword
                {
                    if (matrix[0] != null && UnstableIngot.getInstance().isCorrectItem(matrix[0])
                            && matrix[1] == null
                            && matrix[2] == null
                            && matrix[3] != null && UnstableIngot.getInstance().isCorrectItem(matrix[3])
                            && matrix[4] == null
                            && matrix[5] == null
                            && matrix[6] != null && matrix[6].getType() == Material.OBSIDIAN
                            && matrix[7] == null
                            && matrix[8] == null) {
                        if (isResultSlot) {
                            if (cancellable != null) cancellable.setCancelled(true);
                            ci.setMatrix(getItemStacks(ci.getMatrix()));
                            player.getInventory().addItem(EthericSword.getInstance().getItemStack());
                        } else {
                            ci.setResult(EthericSword.getInstance().getItemStack());
                        }
                    }
                }
                {
                    if (matrix[0] == null
                            && matrix[1] != null && UnstableIngot.getInstance().isCorrectItem(matrix[1])
                            && matrix[2] == null
                            && matrix[3] == null
                            && matrix[4] != null && UnstableIngot.getInstance().isCorrectItem(matrix[4])
                            && matrix[5] == null
                            && matrix[6] == null
                            && matrix[7] != null && matrix[7].getType() == Material.OBSIDIAN
                            && matrix[8] == null) {
                        if (isResultSlot) {
                            if (cancellable != null) cancellable.setCancelled(true);
                            ci.setMatrix(getItemStacks(ci.getMatrix()));
                            player.getInventory().addItem(EthericSword.getInstance().getItemStack());
                        } else {
                            ci.setResult(EthericSword.getInstance().getItemStack());
                        }
                    }
                }
                //player.updateInventory();
            }
        }.runTaskLater(instance, 1); // we need delay! // TODO: why?
    }

    public static ItemStack[] getItemStacks(ItemStack[] items) {
        ItemStack[] newItems = items.clone();
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) continue;
            ItemStack item = newItems[i].clone();
            int amount = Math.min(items[i].getAmount(), 64)-1;
            if (amount <= 0) {
                newItems[i] = null;
            } else {
                item.setAmount(amount);
                newItems[i] = item;
            }
        }
        return newItems;
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent e) {
        ItemStack[] m = e.getInventory().getMatrix().clone();
        boolean stableInMatrix = false;
        for (ItemStack itemStack : m) {
            if (SemiStableNugget.getInstance().isCorrectItem(itemStack)) {
                stableInMatrix = true;
            }
            if (UnstableIngot.getInstance().isCorrectItem(itemStack)) {
                e.setCancelled(true);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        e.getInventory().setMatrix(getItemStacks(e.getInventory().getMatrix()));
                        e.getWhoClicked().getInventory().addItem(EthericSword.getInstance().getItemStack());
                    }
                }.runTaskLater(this, 1);
                break;
            }
        }
        if (!UnstableIngot.getInstance().isCorrectItem(e.getInventory().getResult())
                && !SemiStableNugget.getInstance().isCorrectItem(e.getInventory().getResult())) {
            return;
        }
        boolean finalStableInMatrix = stableInMatrix;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!finalStableInMatrix && UnstableIngot.getInstance().isCorrectItem(e.getInventory().getResult())
                        && !UnstableIngot.getInstance().isTicking(e.getInventory().getResult())) return;
                e.getInventory().setItem(4, DivisionSigil.getInstance().getActiveItemStack());
                //((Player) e.getWhoClicked()).updateInventory();
            }
        }.runTask(this); // return the item after crafting an item
    }
}
