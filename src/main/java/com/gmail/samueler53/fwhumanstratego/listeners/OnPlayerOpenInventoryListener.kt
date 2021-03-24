package com.gmail.samueler53.fwhumanstratego.listeners

import com.gmail.samueler53.fwhumanstratego.managers.GameManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryOpenEvent

class OnPlayerOpenInventoryListener : Listener {

    @EventHandler
    fun onPlayerOpenInventory(event: InventoryOpenEvent) {
        val player = event.player as? Player ?: return
        val game = GameManager.getGameForPlayer(player) ?: return
        val loc = event.inventory.location
        if (loc != game.getTeamFromPlayer(player).roleGui.inventory.location &&
            loc != game.teamGui.inventory.location &&
            loc != game.arena.treasureRedLocation &&
            loc != game.arena.treasureBlueLocation
        ) event.isCancelled = true
    }
}