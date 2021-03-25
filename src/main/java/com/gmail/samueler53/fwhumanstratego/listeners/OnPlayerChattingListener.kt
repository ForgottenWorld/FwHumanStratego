package com.gmail.samueler53.fwhumanstratego.listeners

import com.gmail.samueler53.fwhumanstratego.managers.GameManager
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class OnPlayerChattingListener : Listener {

    @EventHandler
    fun onPlayerChatting(event: AsyncPlayerChatEvent) {
        val game = GameManager.getGameForPlayer(event.player) ?: return
        if (!game.isPlayerInTeam(event.player)) return
        event.isCancelled = true
        val team = game.getTeamForPlayer(event.player)
        val message = when (team) {
            game.redTeam -> "§4Red team %s: %s"
            game.blueTeam -> "§9Blue team %s: %s"
            else -> return
        }
        for (p in team.playersRoles.keys.mapNotNull(Bukkit::getPlayer)) {
            p.sendMessage(message.format(event.player.displayName, event.message))
        }
    }
}