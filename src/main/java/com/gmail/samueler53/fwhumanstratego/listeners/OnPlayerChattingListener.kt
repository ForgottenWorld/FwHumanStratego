package com.gmail.samueler53.fwhumanstratego.listeners

import com.gmail.samueler53.fwhumanstratego.managers.GameManager
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class OnPlayerChattingListener : Listener {

    @EventHandler
    fun onPlayerChatting(event: AsyncPlayerChatEvent) {
        if (GameManager.getGameForPlayer(event.player) == null) return
        val game = GameManager.getGameForPlayer(event.player)
        if (!game!!.isPlayerInTeam(event.player)) return
        event.isCancelled = true
        if (game.redTeam.playersRoles.containsKey(event.player.uniqueId)) {
            for (uuidPlayersInRedTeam in game.redTeam.playersRoles.keys) {
                Bukkit.getPlayer(uuidPlayersInRedTeam)
                    ?.sendMessage("§4Red team ${event.player.displayName}: ${event.message}")
            }
        } else if (game.blueTeam.playersRoles.containsKey(event.player.uniqueId)) {
            for (uuidPlayersInBlueTeam in game.blueTeam.playersRoles.keys) {
                Bukkit.getPlayer(uuidPlayersInBlueTeam)
                    ?.sendMessage("§9Blue team ${event.player.displayName}: ${event.message}")
            }
        }
    }
}