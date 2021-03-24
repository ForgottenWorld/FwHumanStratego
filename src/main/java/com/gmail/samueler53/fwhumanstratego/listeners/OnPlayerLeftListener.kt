package com.gmail.samueler53.fwhumanstratego.listeners

import com.gmail.samueler53.fwhumanstratego.managers.GameManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class OnPlayerLeftListener : Listener {

    @EventHandler
    fun onPlayerLeft(event: PlayerQuitEvent) {
        GameManager.getGameForPlayer(event.player)?.onPlayerLeave(event.player)
    }
}