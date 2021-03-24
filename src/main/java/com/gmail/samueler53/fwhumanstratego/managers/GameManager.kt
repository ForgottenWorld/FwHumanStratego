package com.gmail.samueler53.fwhumanstratego.managers

import com.gmail.samueler53.fwhumanstratego.gui.GamesGui
import com.gmail.samueler53.fwhumanstratego.objects.Arena
import com.gmail.samueler53.fwhumanstratego.objects.Game
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object GameManager {

    private val games = mutableListOf<Game>()

    val gamesGui = GamesGui()

    fun message() {
        for (player in Bukkit.getOnlinePlayers()) {
            val startMessage =
                "${ChatColor.GREEN}${"-".repeat(53)}${
                    ChatColor.DARK_AQUA
                }Ciao ${player.name}, se vuoi giocare a HumanStratego, clicca sotto"

            val clickMessage = TextComponent("\n\n [Clicca qui per giocare]\n").apply {
                color = ChatColor.GREEN
                clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hs join")
                hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("Clicca per giocare"))
            }

            val endMessage = "${ChatColor.GREEN}${"-".repeat(53)}"

            val message = ComponentBuilder()
                .append(startMessage)
                .append(clickMessage)
                .append(endMessage)
                .create()

            player.spigot().sendMessage(*message)
        }
    }

    fun startNewGame(arena: Arena, numberOfPlayers: Int): Game {
        val game = Game(arena, numberOfPlayers)
        games.add(game)
        return game
    }

    fun isArenaBusy(arena: Arena) = games.any { it.arena == arena }

    fun areInTheSameGame(player1: Player, player2: Player) =
        if (this.getGameForPlayer(player1) != null && this.getGameForPlayer(player2) != null) {
            this.getGameForPlayer(player1) == this.getGameForPlayer(player2)
        } else {
            false
        }

    fun getGameForPlayer(player: Player) = games.find { it.isPlayerPlaying(player) }

    fun getGameForArena(arena: Arena) = games.find { it.arena == arena }

    fun removeGame(game: Game) {
        games.remove(game)
    }
}