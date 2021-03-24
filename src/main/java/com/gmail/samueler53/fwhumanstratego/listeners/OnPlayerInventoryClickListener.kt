package com.gmail.samueler53.fwhumanstratego.listeners

import com.gmail.samueler53.fwhumanstratego.FwHumanStratego
import com.gmail.samueler53.fwhumanstratego.managers.GameManager
import com.gmail.samueler53.fwhumanstratego.message.Message
import com.gmail.samueler53.fwhumanstratego.objects.Game
import com.gmail.samueler53.fwhumanstratego.objects.Team
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType.SlotType

class OnPlayerInventoryClickListener : Listener {
    
    @EventHandler
    fun onPlayerInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val game = GameManager.getGameForPlayer(player) ?: return
        if (isClickingOnArmor(event.slotType)) {
            Message.GAME_TOGGLEARMOR.send(player)
            event.isCancelled = true
        }
        try {
            if (player.gameMode == GameMode.SPECTATOR) {
                event.isCancelled = true
            } else if (game.isStarted) {
                val team = game.getTeamFromPlayer(player)
                val treasure = team.treasure
                val otherTeam = game.getOtherTeam(team)
                val otherTreasure = otherTeam.treasure
                if (event.currentItem != null && isStealingTreasure(event.currentItem!!.type, treasure)) {
                    Message.GAME_STEALHISWOOL.send(player)
                    event.isCancelled = true
                } else if (
                    event.clickedInventory!!.location == game.arena.treasureBlueLocation &&
                    team.type == Team.Type.RED
                ) {
                    if (isStealingTreasure(event.currentItem!!.type, otherTreasure)) {
                        Message.GAME_STEALEDWOOLBLUE.broadcast(game, player.displayName)
                    }
                } else if (
                    event.clickedInventory!!.location == game.arena.treasureRedLocation &&
                    team.type == Team.Type.BLUE
                ) {
                    if (isStealingTreasure(event.currentItem!!.type, otherTreasure)) {
                        Message.GAME_STEALEDWOOLRED.broadcast(game, player.displayName)
                    }
                } else if (
                    event.view.topInventory.location != null &&
                    event.currentItem != null &&
                    (event.currentItem!!.type == Material.RED_WOOL ||
                        event.currentItem!!.type == Material.BLUE_WOOL) &&
                    event.view.topInventory.location != game.arena.treasureRedLocation &&
                    event.view.topInventory.location != game.arena.treasureBlueLocation
                ) {
                    event.isCancelled = true
                }
                if (event.inventory.location == game.arena.treasureRedLocation &&
                    team.type == Team.Type.RED || event.inventory.location == game.arena.treasureBlueLocation &&
                    team.type == Team.Type.BLUE
                ) {
                    Bukkit.getScheduler().runTaskLater(FwHumanStratego.plugin, Runnable {
                        if (event.inventory.contains(otherTreasure)) {
                            stoleWool(team, game)
                        }
                    }, 1)
                }
            }
        } catch (ignored: Exception) {
        }
    }

    private fun isClickingOnArmor(slotType: SlotType): Boolean {
        return slotType == SlotType.ARMOR
    }

    private fun isStealingTreasure(currentItem: Material, treasure: Material?): Boolean {
        return currentItem == treasure
    }

    private fun stoleWool(team: Team, game: Game) {
        Message.GAME_TREASURESTEALED.broadcast(game)
        team.addPoints(FwHumanStratego.defaultConfig.getInt("pointsFromTreasure"))
        game.endRound(team)
    }
}