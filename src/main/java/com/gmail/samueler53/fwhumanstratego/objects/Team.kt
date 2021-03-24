package com.gmail.samueler53.fwhumanstratego.objects

import com.gmail.samueler53.fwhumanstratego.gui.RoleGui
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import java.util.*

class Team(
    val type: Type,
    game: Game
) {

    enum class Type { RED, BLUE }

    val treasure = when (type) {
        Type.RED -> Material.RED_WOOL
        Type.BLUE -> Material.BLUE_WOOL
    }

    var points = 0

    var playersRoles = mutableMapOf<UUID, Role?>()

    var rolesRemaining = mutableMapOf<Role, Int>()

    private val kit = ItemStack(Material.LEATHER_CHESTPLATE).apply {
        val color = when (this@Team.type) {
            Type.RED -> Color.RED
            Type.BLUE -> Color.BLUE
        }
        itemMeta = (itemMeta as LeatherArmorMeta).apply {
            setColor(color)
        }
    }

    val roleGui = RoleGui(this, game)

    fun addPlayer(player: Player) {
        playersRoles[player.uniqueId] = null
    }

    fun addPlayerRole(player: Player, role: Role) {
        playersRoles[player.uniqueId] = role
    }

    fun removePlayer(player: Player) {
        playersRoles.remove(player.uniqueId)
    }

    fun addPoints(points: Int) {
        this.points += points
    }

    fun clearRoles() {
        playersRoles.replaceAll { _,_ -> null }
    }

    fun equipKitForEachPlayer() {
        playersRoles.keys
            .mapNotNull(Bukkit::getPlayer)
            .forEach { it.inventory.chestplate = kit }
    }
}