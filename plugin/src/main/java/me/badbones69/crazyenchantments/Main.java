package me.badbones69.crazyenchantments;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.currencyapi.CurrencyAPI;
import me.badbones69.crazyenchantments.api.objects.CEPlayer;
import me.badbones69.crazyenchantments.commands.*;
import me.badbones69.crazyenchantments.controllers.*;
import me.badbones69.crazyenchantments.enchantments.*;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import me.badbones69.crazyenchantments.multisupport.Version;
import me.badbones69.crazyenchantments.multisupport.anticheats.AACSupport;
import me.badbones69.premiumhooks.anticheat.DakataAntiCheatSupport;
import me.badbones69.premiumhooks.spawners.SilkSpawnerSupport;
import me.badbones69.premiumhooks.spawners.SilkSpawnersCandcSupport;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener {

    private final CrazyEnchantments ce = CrazyEnchantments.getInstance();
    private final FileManager fileManager = FileManager.getInstance();
    private boolean fixHealth;

    @Override
    public void onEnable() {
        if (!Version.getCurrentVersion().isSupported()) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + String.format("The current version of %s only supports up to %s", getName(), Version.getLatestVersion()));
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Contact the developer of this plugin to request an update");
            return;
        }
        fileManager.logInfo(true).setup(this);
        ce.load();
        SupportedPlugins.printHooks();
        Methods.hasUpdate();
        CurrencyAPI.loadCurrency();
        fixHealth = Files.CONFIG.getFile().getBoolean("Settings.Reset-Players-Max-Health");
        for (Player player : Bukkit.getOnlinePlayers()) {
            ce.loadCEPlayer(player);
            if (fixHealth) {
                if (ce.useHealthAttributes()) {
                    player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
                } else {
                    player.setMaxHealth(20);
                }
            }
        }
        getCommand("crazyenchantments").setExecutor(new CECommand());
        getCommand("crazyenchantments").setTabCompleter(new CETab());
        getCommand("tinkerer").setExecutor(new TinkerCommand());
        getCommand("blacksmith").setExecutor(new BlackSmithCommand());
        getCommand("gkit").setExecutor(new GkitzCommand());
        getCommand("gkit").setTabCompleter(new GkitzTab());
        PluginManager pm = Bukkit.getServer().getPluginManager();
        //==========================================================================\\
        registerEvents(this,
                new ShopControl(),
                new InfoGUIControl(),
                new LostBookController(),
                new EnchantmentControl(),
                new SignControl(),
                new DustControl(),
                new Tinkerer(),
                new AuraListener(),
                new ScrollControl(),
                new BlackSmith(),
                new ArmorListener(),
                new ProtectionCrystal(),
                new Scrambler(),
                new CommandChecker(),
                new FireworkDamage());
        if (ce.isGkitzEnabled()) {
            registerEvents(new GKitzController());
        }
        //==========================================================================\\
        registerEvents(new Bows(),
                new Axes(),
                new Tools(),
                new Hoes(),
                new Helmets(),
                new PickAxes(),
                new Boots(),
                new Armor(),
                new Swords(),
                new AllyEnchantments());
        if (SupportedPlugins.AAC.isPluginLoaded()) {
            registerEvents(new AACSupport());
        }
        if (SupportedPlugins.SILK_SPAWNERS.isPluginLoaded()) {
            registerEvents(new SilkSpawnerSupport());
        }
        if (SupportedPlugins.SILK_SPAWNERS_CANDC.isPluginLoaded()) {
            registerEvents(new SilkSpawnersCandcSupport());
        }
        if (SupportedPlugins.DAKATA.isPluginLoaded()) {
            registerEvents(new DakataAntiCheatSupport());
        }
        //==========================================================================\\
        new Metrics(this);// Starts up bStats
        new BukkitRunnable() {
            @Override
            public void run() {
                for (CEPlayer player : ce.getCEPlayers()) {
                    ce.backupCEPlayer(player);
                }
            }
        }.runTaskTimerAsynchronously(this, 5 * 20 * 60, 5 * 20 * 60);
    }

    private void registerEvents(Listener... listeners) {
        final PluginManager manager = Bukkit.getPluginManager();
        for (Listener listener: listeners) {
            manager.registerEvents(listener, this);
        }
    }

    @Override
    public void onDisable() {
        if (!Version.getCurrentVersion().isSupported()) {
            return;
        }
        if (ce.getAllyManager() != null) {
            ce.getAllyManager().forceRemoveAllies();
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            ce.unloadCEPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        ce.loadCEPlayer(player);
        ce.updatePlayerEffects(player);
        if (fixHealth) {
            if (ce.useHealthAttributes()) {
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
            } else {
                player.setMaxHealth(20);
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getName().equals("BadBones69")) {
                    player.sendMessage(Methods.getPrefix() + Methods.color("&7This server is running your Crazy Enchantments Plugin. "
                            + "&7It is running version &av" + ce.getPlugin().getDescription().getVersion() + "&7."));
                }
                if (player.isOp()) {
                    Methods.hasUpdate(player);
                }
            }
        }.
                runTaskLaterAsynchronously(this, 20);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        ce.unloadCEPlayer(e.getPlayer());
    }

}