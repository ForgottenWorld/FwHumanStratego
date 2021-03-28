package com.gmail.samueler53.fwhumanstratego.configuration

import com.gmail.samueler53.fwhumanstratego.FwHumanStratego
import com.gmail.samueler53.fwhumanstratego.managers.RoleManager
import org.bukkit.plugin.java.JavaPlugin

object Configuration {

    var rounds: Int = -1
        private set

    var delayGameStart: Long = -1
        private set

    var pointsFromTreasure: Int = -1
        private set

    var allowRoleChanges: Boolean = false
        private set

    fun load(plugin: JavaPlugin) {
        rounds = plugin.config.getInt("pointsFromTreasure", 4)
        delayGameStart = plugin.config.getInt("delayStartGame", 300).toLong()
        pointsFromTreasure = plugin.config.getInt("pointsFromTreasure", 1500)
        allowRoleChanges = plugin.config.getBoolean("allowRoleChanges", false)
        RoleManager.loadRolesFromConfig(plugin.config)
    }

    fun reload() {
        val plugin = FwHumanStratego.instance
        plugin.reloadConfig()
        load(plugin)
    }
}