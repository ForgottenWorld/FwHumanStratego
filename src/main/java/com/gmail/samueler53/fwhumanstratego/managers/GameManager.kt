package com.gmail.samueler53.fwhumanstratego.managers

import com.gmail.samueler53.fwhumanstratego.configuration.Configuration
import com.gmail.samueler53.fwhumanstratego.gui.GamesGui
import com.gmail.samueler53.fwhumanstratego.message.Message
import com.gmail.samueler53.fwhumanstratego.objects.Arena
import com.gmail.samueler53.fwhumanstratego.objects.Game
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

object GameManager {

    private val arenaGames = mutableMapOf<String, Game>()

    private val playerGames = mutableMapOf<UUID, Game>()

    val gamesGui = GamesGui.newInstance()

    private fun advertiseNewGame() {
        val clickMessage = ComponentBuilder("[Clicca qui per giocare]")
            .color(ChatColor.GREEN)
            .event(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hs join"))
            .event(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("Clicca per giocare")))
            .create()

        val endMessage = "\n§a${"-".repeat(53)}"

        for (player in Bukkit.getOnlinePlayers()) {
            val startMessage =
                "§a${"-".repeat(53)}§3Ciao ${player.name}, se vuoi giocare a HumanStratego, clicca sotto\n\n "

            val message = ComponentBuilder()
                .append(startMessage)
                .append(clickMessage)
                .append(endMessage)
                .create()

            player.spigot().sendMessage(*message)
        }
    }

    fun onPlayerStartNewGame(player: Player, arena: Arena, numberOfPlayers: Int) {
        if (arenaGames.size >= 9) {
            Message.GAME_NO_MORE_GAMES.send(player)
            return
        }

        if (numberOfPlayers % 2 != 0 || numberOfPlayers <= 1) {
            Message.GAME_ODD_PLAYERS.send(player)
            return
        }

        if (arenaGames.containsKey(arena.name)) {
            Message.GAME_ARENA_BUSY.send(player)
            return
        }

        val game = Game(arena, numberOfPlayers)
        arenaGames[arena.name] = game
        advertiseNewGame()
        gamesGui.update()
    }

    fun onAllPlayersForceRemoved(reason: String?) {
        for (player in playerGames.keys.mapNotNull(Bukkit::getPlayer)) {
            if (reason == null) {
                player.sendMessage("La partita è stata interrotta.")
            } else {
                player.sendMessage("La partita è stata interrotta per il seguento motivo: $reason.")
            }
        }
        for (game in getAllGames()) {
            game.onGameStopped(null)
        }
    }

    fun onPlayerForceRemoved(player: Player, reason: String?) {
        if (reason == null) {
            player.sendMessage("Sei stato rimosso dalla partita.")
        } else {
            player.sendMessage("Sei stato rimosso dalla partita per il seguento motivo: $reason.")
        }
        playerGames[player.uniqueId]?.onPlayerLeave(player, true)
    }

    fun reserveGameForEchelon(): Game {
        if (arenaGames.size >= 9) {
            arenaGames.values.random().onGameStopped(null)
        }

        val arena = ArenaManager.getRandomFreeArena()

        val game = Game(
            arena,
            Configuration.echelonMinigameRotationNumberOfPlayers,
            false
        )

        arenaGames[arena.name] = game
        return game
    }

    fun getAllGames(): Collection<Game> = arenaGames.values

    fun setGameForPlayer(player: Player, game: Game) {
        playerGames[player.uniqueId] = game
        gamesGui.update()
    }

    fun removePlayerFromGame(player: Player) {
        playerGames.remove(player.uniqueId)
        gamesGui.update()
    }

    fun getGameForPlayer(player: Player) = playerGames[player.uniqueId]

    fun getGameForArena(arena: Arena) = arenaGames[arena.name]

    fun removeGame(arena: Arena) {
        arenaGames.remove(arena.name)
        gamesGui.update()
    }
}