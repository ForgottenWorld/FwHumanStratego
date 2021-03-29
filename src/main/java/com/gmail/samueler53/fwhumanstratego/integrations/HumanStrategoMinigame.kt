package com.gmail.samueler53.fwhumanstratego.integrations

import com.gmail.samueler53.fwhumanstratego.managers.GameManager
import com.gmail.samueler53.fwhumanstratego.objects.Game
import it.forgottenworld.echelonapi.FWEchelon
import it.forgottenworld.echelonapi.minigame.Minigame
import org.bukkit.entity.Player

class HumanStrategoMinigame : Minigame {

    override val description = "Gioco di strategia a squadre. È richiesto un alto grado di collaborazione!"

    override val id = MINIGAME_ID

    override val name = MINIGAME_NAME

    override val maxPlayers = 16

    override val minPlayers = 16

    var game: Game? = null


    override fun canStart() = true

    override fun onAborted(players: List<Player>) {
        game?.onGameStopped(null)
    }

    override fun onAnnounced() {
        // nothing to do here
    }

    override fun onPickedForRotation() {
        game = GameManager.reserveGameForEchelon()
        FWEchelon.api.minigameService.notifyReadyForAnnouncement(this)
    }

    override fun onPlayerJoinLobby(player: Player, players: List<Player>) {
        game?.onPlayerJoin(player)
    }

    override fun onPlayerLeaveLobby(player: Player, players: List<Player>) {
        game?.onPlayerLeave(player, false)
    }

    override fun onStart(players: List<Player>) {
        // do nothing. the game is started
        // automatically as there are enough players
    }

    companion object {
        private const val MINIGAME_ID = "FW_HUMAN_STRATEGO"
        private const val MINIGAME_NAME = "HumanStratego"
    }
}