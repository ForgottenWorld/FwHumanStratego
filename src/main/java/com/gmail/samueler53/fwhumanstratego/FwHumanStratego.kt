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
        plugin = this
        dataLoad()
        loadConfiguration()
        registerCommands()        
        with (server.pluginManager) {
            registerEvents(OnPlayerAttackedListener(), plugin)
            registerEvents(OnPlayerChattingListener(), plugin)
            registerEvents(InGameActionBlocker(), plugin)
            registerEvents(OnPlayerInventoryClickListener(), plugin)
            registerEvents(OnPlayerLeftListener(), plugin)
            registerEvents(OnPlayerOpenInventoryListener(), plugin)
            registerEvents(OnPlayerTeleportListener(), plugin)
        }
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

    private fun dataLoad() {
        data = Data.load()
    }

    companion object {
        val dataSavePath get() = "${plugin.dataFolder.path}/saved.yml"

        lateinit var defaultConfig: FileConfiguration
        lateinit var data: Data
        lateinit var plugin: FwHumanStratego
    }
}