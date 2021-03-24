package com.gmail.samueler53.fwhumanstratego

import com.gmail.samueler53.fwhumanstratego.commands.AdminCommand
import com.gmail.samueler53.fwhumanstratego.commands.UserCommand
import com.gmail.samueler53.fwhumanstratego.data.Data
import com.gmail.samueler53.fwhumanstratego.listeners.InGameActionBlocker
import com.gmail.samueler53.fwhumanstratego.listeners.OnPlayerAttackedListener
import com.gmail.samueler53.fwhumanstratego.listeners.OnPlayerChattingListener
import com.gmail.samueler53.fwhumanstratego.listeners.OnPlayerInventoryClickListener
import com.gmail.samueler53.fwhumanstratego.listeners.OnPlayerLeftListener
import com.gmail.samueler53.fwhumanstratego.listeners.OnPlayerOpenInventoryListener
import com.gmail.samueler53.fwhumanstratego.listeners.OnPlayerTeleportListener
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

class FwHumanStratego : JavaPlugin() {
    override fun onEnable() {
        // Plugin startup logic
        plugin = this
        dataLoad()
        loadConfiguration()
        registerCommands()
        registerListeners()
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    private fun loadConfiguration() {
        config.options().copyDefaults(true)
        saveDefaultConfig()
        defaultConfig = config
    }

    fun reloadDefaultConfig() {
        reloadConfig()
        defaultConfig = config
    }

    private fun registerCommands() {
        getCommand("humanstratego")!!.setExecutor(UserCommand())
        getCommand("humanstrategoadmin")!!.setExecutor(AdminCommand(this))
    }

    private fun registerListeners() {
        server.pluginManager.registerEvents(OnPlayerAttackedListener(), this)
        server.pluginManager.registerEvents(OnPlayerChattingListener(), this)
        server.pluginManager.registerEvents(InGameActionBlocker(), this)
        server.pluginManager.registerEvents(OnPlayerInventoryClickListener(), this)
        server.pluginManager.registerEvents(OnPlayerLeftListener(), this)
        server.pluginManager.registerEvents(OnPlayerOpenInventoryListener(), this)
        server.pluginManager.registerEvents(OnPlayerTeleportListener(), this)
    }

    private fun dataLoad() {
        data = Data.loadData(dataFolder.path + "/Saved.data")
    }

    companion object {
        lateinit var defaultConfig: FileConfiguration
        lateinit var data: Data
        lateinit var plugin: FwHumanStratego
    }
}