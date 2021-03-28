package com.gmail.samueler53.fwhumanstratego.listeners

import com.gmail.samueler53.fwhumanstratego.managers.ArenaManager
import com.gmail.samueler53.fwhumanstratego.managers.GameManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerQuitEvent

class GameEventListener : Listener {

    @EventHandler
    fun onPlayerAttacked(event: EntityDamageByEntityEvent) {
        val attacker = event.damager as? Player ?: return
        val attacked = event.entity as? Player ?: return

        val attackerGame = GameManager.getGameForPlayer(attacker) ?: return
        val attackedGame = GameManager.getGameForPlayer(attacked)
        if (attackerGame !== attackedGame) return

        event.isCancelled = true

        attackerGame.onPlayerAttackPlayer(attacker, attacked)
    }

    @EventHandler
    fun onAsyncPlayerChat(event: AsyncPlayerChatEvent) {
        GameManager.getGameForPlayer(event.player)?.onPlayerChat(event)
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        GameManager.getGameForPlayer(player)?.onInventoryClick(player, event)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        GameManager.getGameForPlayer(event.player)?.onPlayerLeave(event.player, true)
        ArenaManager.onPlayerStopBuildingArena(event.player)
    }

    @EventHandler
    fun onInventoryOpen(event: InventoryOpenEvent) {
        val player = event.player as? Player ?: return
        GameManager.getGameForPlayer(player)?.onInventoryOpen(event)
    }
}