package com.gmail.samueler53.fwhumanstratego.listeners

import com.gmail.samueler53.fwhumanstratego.managers.GameManager
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent

class GameActionBlocker : Listener {

    private fun cancelEventIfPlayerInGame(
        player: Player,
        event: Cancellable
    ) {
        if (GameManager.getGameForPlayer(player) == null) return
        event.isCancelled = true
    }

    @EventHandler
    fun onEntityDamaged(event: EntityDamageEvent) {
        val player = event.entity as? Player ?: return
        cancelEventIfPlayerInGame(player, event)
    }

    @EventHandler
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        cancelEventIfPlayerInGame(event.player, event)
    }

    @EventHandler
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        val player = event.entity as? Player ?: return
        cancelEventIfPlayerInGame(player, event)
    }

    @EventHandler
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        cancelEventIfPlayerInGame(event.player, event)
    }
}