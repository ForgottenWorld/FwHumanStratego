package com.gmail.samueler53.fwhumanstratego

import com.gmail.samueler53.fwhumanstratego.commands.AdminCommand
import com.gmail.samueler53.fwhumanstratego.commands.UserCommand
import com.gmail.samueler53.fwhumanstratego.configuration.Configuration
import com.gmail.samueler53.fwhumanstratego.listeners.GameActionBlocker
import com.gmail.samueler53.fwhumanstratego.listeners.GameEventListener
import com.gmail.samueler53.fwhumanstratego.managers.ArenaManager
import org.bukkit.plugin.java.JavaPlugin

class FwHumanStratego : JavaPlugin() {

    override fun onEnable() {
        ArenaManager.loadData()

        saveDefaultConfig()

        Configuration.load(this)

        getCommand("humanstratego")!!.setExecutor(UserCommand())
        getCommand("humanstrategoadmin")!!.setExecutor(AdminCommand())

        server.pluginManager.registerEvents(GameEventListener(), instance)
        server.pluginManager.registerEvents(GameActionBlocker(), instance)
    }

    companion object {

        val instance get() = getPlugin(FwHumanStratego::class.java)
    }
}