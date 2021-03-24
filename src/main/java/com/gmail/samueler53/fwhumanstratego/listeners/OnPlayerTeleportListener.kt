package com.gmail.samueler53.fwhumanstratego.listeners

import com.gmail.samueler53.fwhumanstratego.FwHumanStratego
import com.gmail.samueler53.fwhumanstratego.managers.GameManager
import com.gmail.samueler53.fwhumanstratego.message.Message
import com.gmail.samueler53.fwhumanstratego.objects.Game
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class OnPlayerTeleportListener : Listener {

    @EventHandler
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        val game = GameManager.getGameForPlayer(event.player) ?: return
        if (game.arena.lobbyLocation == event.to) {
            lobbyTeleport(event.player, game)
        } else if (
            game.arena.redTeamLocation == event.to ||
            game.arena.blueTeamLocation == event.to
        ) {
            spawnTeleport(event.player, game)
        }
    }

    private fun lobbyTeleport(player: Player, game: Game) {
        Message.GAME_CHOOSETEAM.send(player)
        val teamGui = game.teamGui
        Bukkit.getScheduler()
            .runTaskLater(FwHumanStratego.plugin, Runnable { teamGui.show(player) }, 1L)
    }

    private fun spawnTeleport(player: Player, game: Game) {
        Message.GAME_CHOOSEROLE.send(player)
        val team = game.getTeamFromPlayer(player)
        Bukkit.getScheduler()
            .runTaskLater(FwHumanStratego.plugin, Runnable { team.roleGui.show(player) }, 1L)
        if (game.isSpectateMode(team)) {
            player.gameMode = GameMode.SPECTATOR
        }
        selectionRoleEffects(player)
    }

    private fun selectionRoleEffects(player: Player) {
        player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 2147483647, 250))
        player.walkSpeed = 0.0f
        player.foodLevel = 4
    }
}