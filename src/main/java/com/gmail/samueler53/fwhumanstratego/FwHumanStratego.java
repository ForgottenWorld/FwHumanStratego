package com.gmail.samueler53.fwhumanstratego;

import com.gmail.samueler53.fwhumanstratego.commands.AdminCommand;
import com.gmail.samueler53.fwhumanstratego.commands.UserCommand;
import com.gmail.samueler53.fwhumanstratego.data.Data;
import com.gmail.samueler53.fwhumanstratego.listeners.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class FwHumanStratego extends JavaPlugin {

    public static FileConfiguration defaultConfig;
    public static Data data;
    public static FwHumanStratego plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        dataLoad();
        loadConfiguration();
        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void loadConfiguration() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        defaultConfig = getConfig();
    }

    public void reloadDefaultConfig() {
        reloadConfig();
        defaultConfig = getConfig();
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("humanstratego")).setExecutor(new UserCommand());
        Objects.requireNonNull(getCommand("humanstrategoadmin")).setExecutor(new AdminCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new onPlayerAttackedListener(), this);
        getServer().getPluginManager().registerEvents(new onPlayerBreakingListener(), this);
        getServer().getPluginManager().registerEvents(new onPlayerChattingListener(), this);
        getServer().getPluginManager().registerEvents(new onPlayerDamagedListener(), this);
        getServer().getPluginManager().registerEvents(new onPlayerDroppingListener(), this);
        getServer().getPluginManager().registerEvents(new onPlayerFoodLevelChangeListener(), this);
        getServer().getPluginManager().registerEvents(new onPlayerInventoryClickListener(), this);
        getServer().getPluginManager().registerEvents(new onPlayerLeftListener(), this);
        getServer().getPluginManager().registerEvents(new onPlayerOpeningInventoriesListener(), this);
        getServer().getPluginManager().registerEvents(new onPlayerPlacingListener(), this);
        getServer().getPluginManager().registerEvents(new onPlayerTeleportListener(), this);
    }

    public static FileConfiguration getDefaultConfig() {
        return defaultConfig;
    }

    public void dataLoad() {
        data = Data.loadData(getDataFolder().getPath() + "/Saved.data");
    }

    public static Data getData() {
        return data;
    }

    public static FwHumanStratego getPlugin() {
        return plugin;
    }
}
