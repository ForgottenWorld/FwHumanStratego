package com.gmail.samueler53.fwhumanstratego.objects

import com.gmail.samueler53.fwhumanstratego.managers.RoleManager
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Material
import org.bukkit.entity.Player

data class Role(
    val name: String,
    val points: Int,
    val canAttack: Boolean,
    val counterattacks: Boolean,
    val isVital: Boolean,
    val uses: Int,
    val minPlayersToActivate: Int,
    val description: String,
    val canKillString: String,
    val minPlayers: Int,
    val maxPlayers: Int,
    val material: Material
) {

    val displayName = name.capitalize()

    private val canKillRoles by lazy {
        canKillString.split(",").map {
            RoleManager.roles[it.toLowerCase()]!!
        }
    }

    fun canKill(role: Role) = canKillRoles.contains(role)

    fun sendInfoLinkToPlayer(player: Player) {
        val msg = ComponentBuilder("[INFORMAZIONI SUL RUOLO]")
            .color(ChatColor.GREEN)
            .event(ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/hs info $name"
            ))
            .event(HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                Text("Clicca per avere informazioni sul tuo ruolo")
            ))
            .create()
        player.spigot().sendMessage(*msg)
    }
}