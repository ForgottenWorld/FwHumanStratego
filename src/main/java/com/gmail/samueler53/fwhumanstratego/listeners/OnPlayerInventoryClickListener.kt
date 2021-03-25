package com.gmail.samueler53.fwhumanstratego.listeners

import com.gmail.samueler53.fwhumanstratego.FwHumanStratego
import com.gmail.samueler53.fwhumanstratego.managers.GameManager
import com.gmail.samueler53.fwhumanstratego.message.Message
import com.gmail.samueler53.fwhumanstratego.objects.Team
import com.gmail.samueler53.fwhumanstratego.utils.launch
import kotlinx.coroutines.delay
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

        if (event.slotType == SlotType.ARMOR) {
            Message.GAME_TOGGLEARMOR.send(player)
            event.isCancelled = true
        }

        if (player.gameMode == GameMode.SPECTATOR) {
            event.isCancelled = true
            return
        }

        if (!game.isStarted) return
        val team = game.getTeamForPlayer(player)

        with(event) {
            when {
                currentItem != null && currentItem!!.type == team.treasure -> {
                    Message.GAME_STEALHISWOOL.send(player)
                    isCancelled = true
                }
                clickedInventory!!.location == game.arena.treasureBlueLocation &&
                    team.type == Team.Type.RED -> {
                    if (currentItem!!.type == game.getOtherTeam(team).treasure) {
                        Message.GAME_STEALEDWOOLBLUE.broadcast(game, player.displayName)
                    }
                }
                clickedInventory!!.location == game.arena.treasureRedLocation &&
                    team.type == Team.Type.BLUE -> {
                    if (currentItem!!.type == game.getOtherTeam(team).treasure) {
                        Message.GAME_STEALEDWOOLRED.broadcast(game, player.displayName)
                    }
                }
                view.topInventory.location != null && currentItem != null &&
                    (currentItem!!.type == Material.RED_WOOL || currentItem!!.type == Material.BLUE_WOOL) &&
                    view.topInventory.location != game.arena.treasureRedLocation &&
                    view.topInventory.location != game.arena.treasureBlueLocation -> {
                    isCancelled = true
                }
            }

            if (inventory.location == game.arena.treasureRedLocation &&
                team.type == Team.Type.RED || inventory.location == game.arena.treasureBlueLocation &&
                team.type == Team.Type.BLUE
            ) {
                if (inventory.contains(game.getOtherTeam(team).treasure)) {
                    launch {
                        delay(1)
                        Message.GAME_TREASURESTEALED.broadcast(game)
                        team.addPoints(FwHumanStratego.defaultConfig.getInt("pointsFromTreasure"))
                        game.endRound(team)
                    }
                }
            }
        }
    }
}