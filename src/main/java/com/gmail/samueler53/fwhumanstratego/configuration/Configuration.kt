package com.gmail.samueler53.fwhumanstratego.configuration

import com.gmail.samueler53.fwhumanstratego.FwHumanStratego
import com.gmail.samueler53.fwhumanstratego.managers.RoleManager
import org.bukkit.Bukkit
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

    var enableFWEchelonIntegration: Boolean = false
        private set

    var enableEchelonMutex: Boolean = false
        private set

    var enableEchelonMinigames: Boolean = false
        private set

    var echelonMinigameRotationNumberOfPlayers: Int = 16
        private set

    fun load(plugin: JavaPlugin) {
        rounds = plugin.config.getInt("pointsFromTreasure", 4)
        delayGameStart = plugin.config.getInt("delayStartGame", 300).toLong()
        pointsFromTreasure = plugin.config.getInt("pointsFromTreasure", 1500)
        allowRoleChanges = plugin.config.getBoolean("allowRoleChanges", false)
        enableFWEchelonIntegration = plugin.config.getBoolean("enableFWEchelonIntegration", false)
        enableEchelonMutex = plugin.config.getBoolean("enableEchelonMutex", false)
        enableEchelonMinigames = plugin.config.getBoolean("enableEchelonMinigames", false)

        echelonMinigameRotationNumberOfPlayers = plugin.config
            .getInt("echelonMinigameRotationNumberOfPlayers", 16)
        if (echelonMinigameRotationNumberOfPlayers % 2 != 0) {
            Bukkit.getLogger().warning(
                "echelonMinigameRotationNumberOfPlayers was not an even number. It has been set to 16 instead."
            )
            echelonMinigameRotationNumberOfPlayers = 16
        }

        RoleManager.loadRolesFromConfig(plugin.config)
    }

    fun reload() {
        val plugin = FwHumanStratego.instance
        plugin.reloadConfig()
        load(plugin)
    }
}