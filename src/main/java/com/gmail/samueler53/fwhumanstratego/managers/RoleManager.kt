package com.gmail.samueler53.fwhumanstratego.managers

import com.gmail.samueler53.fwhumanstratego.objects.Role
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection

object RoleManager {

    var roles = mapOf<String, Role>()
        private set

    fun getRoleByName(roleName: String) = roles[roleName]

    fun loadRolesFromConfig(configuration: ConfigurationSection) {
        val rolesByName = mutableMapOf<String, Role>()
        with (configuration.getConfigurationSection("roles")!!) {
            for (key in getKeys(false)) {
                with (getConfigurationSection(key)!!) {
                    val roleName = key.toLowerCase()
                    val role = Role(
                        name = roleName,
                        points = getInt("points"),
                        description = getString("description")!!,
                        canAttack = getBoolean("can_attack"),
                        counterattacks = getBoolean("counterattacks"),
                        isVital = getBoolean("isVital"),
                        minPlayers = getInt("min_players"),
                        maxPlayers = getInt("max_players"),
                        minPlayersToActivate = getInt("min_players_to_activate"),
                        material = Material.getMaterial(getString("material")!!)!!,
                        canKillString = getString("can_kill")!!,
                    )
                    rolesByName[roleName] = role
                }
            }
        }
        roles = rolesByName
    }
}