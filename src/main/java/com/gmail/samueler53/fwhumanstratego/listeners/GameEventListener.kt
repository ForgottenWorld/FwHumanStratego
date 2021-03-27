package com.gmail.samueler53.fwhumanstratego.listeners

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
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

        val game = GameManager.getGameForPlayer(attacked)
        if (game?.containsPlayer(attacker) != true) return

        event.isCancelled = true

        game.onPlayerAttackPlayer(attacker, attacked)
    }

    @EventHandler
    fun onPlayerChatting(event: AsyncPlayerChatEvent) {
        GameManager.getGameForPlayer(event.player)?.onPlayerChat(event)
    }

    @EventHandler
    fun onPlayerInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        GameManager.getGameForPlayer(player)?.onInventoryClick(player, event)
    }

    @EventHandler
    fun onPlayerLeft(event: PlayerQuitEvent) {
        GameManager.getGameForPlayer(event.player)?.onPlayerLeave(event.player, true)
    }

    @EventHandler
    fun onPlayerOpenInventory(event: InventoryOpenEvent) {
        val player = event.player as? Player ?: return
        GameManager.getGameForPlayer(player) ?: return
        if (event.inventory.holder is ChestGui) return
    }
}