package com.gmail.samueler53.fwhumanstratego.listeners

import com.gmail.samueler53.fwhumanstratego.managers.ArenaManager
import com.gmail.samueler53.fwhumanstratego.managers.GameManager
import com.gmail.samueler53.fwhumanstratego.message.Message
import com.gmail.samueler53.fwhumanstratego.objects.Game
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class OnPlayerAttackedListener : Listener {

    @EventHandler
    fun onPlayerAttacked(event: EntityDamageByEntityEvent) {
        val damageDealer = event.damager as? Player ?: return
        val damaged = event.entity as? Player ?: return

        if (!GameManager.areInTheSameGame(damaged, damageDealer)) return
        val game = GameManager.getGameForPlayer(damaged) ?: return

        event.isCancelled = true

        if (!game.isStarted) {
            Message.GAME_PREPARATIONFASE.send(damageDealer)
            return
        }

        if (game.areInTheSameTeam(damageDealer, damaged)) {
            Message.GAME_SAMETEAM.send(damageDealer)
            return
        }

        if (game.getRoleFromPlayer(damageDealer) == null ||
            game.getRoleFromPlayer(damaged) == null) return

        val roleDamageDealer = game.getRoleFromPlayer(damageDealer)
        val roleDamaged = game.getRoleFromPlayer(damaged)

        if (roleDamaged!!.name != roleDamageDealer!!.name && !game.isBomb(damageDealer) && !(game.isGeneral(
                damageDealer
            ) && game.isBomb(damaged))
        ) {
            winnerThing(game.getWhoWin(damageDealer, damaged), game)
            looserThing(game.getWhoLose(damageDealer, damaged), game)
        } else if (game.isBomb(damageDealer)) {
            Message.GAME_BOMB.send(damageDealer)
        }
    }

    private fun winnerThing(winner: Player, game: Game) {
        winner.playSound(winner.location, Sound.ENTITY_PLAYER_ATTACK_CRIT, 10.0f, 10.0f)
        if (game.isBomb(winner)) {
            winner.world.createExplosion(winner.location, 0.0f)
        }
    }

    private fun looserThing(loser: Player, game: Game) {
        val hisTeam = game.getTeamForPlayer(loser)
        val otherTeam = game.getOtherTeam(hisTeam)
        otherTeam.addPoints(game.getRoleFromPlayer(loser)!!.points)
        game.scoreboard.removeScoreboardForPlayer(loser)
        ArenaManager.teleportPlayerToHisSpawnPoint(loser, game)
        if (game.isGeneral(loser)) {
            Message.GAME_GENERALDEAD.broadcast(game)
            game.endRound(otherTeam)
        } else if (game.hasStoleWool(loser)) {
            game.stolenWool(loser)
            Message.GAME_TREASURESAVED.broadcast(game, loser.displayName)
        }
        hisTeam.playersRoles[loser.uniqueId] = null
        hisTeam.roleGui.updateGui()
    }
}