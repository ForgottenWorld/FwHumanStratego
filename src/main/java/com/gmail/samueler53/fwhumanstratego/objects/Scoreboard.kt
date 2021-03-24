package com.gmail.samueler53.fwhumanstratego.objects

import com.gmail.samueler53.fwhumanstratego.message.Message
import fr.mrmicky.fastboard.FastBoard
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

class Scoreboard internal constructor(private val game: Game) {

    private val boards = mutableMapOf<UUID, FastBoard>()

    fun initScoreboards() {
        game.playersPlaying
            .mapNotNull(Bukkit::getPlayer)
            .forEach {
                boards[it.uniqueId] = FastBoard(it).apply {
                    updateTitle(Message.PREFIX.message)
                }
            }
    }

    fun removeScoreboards() {
        boards.values.forEach { it.delete() }
        boards.clear()
    }

    fun removeScoreboardForPlayer(player: Player) {
        with (boards[player.uniqueId] ?: return) {
            repeat(size(), ::removeLine)
        }
    }

    fun updatePlayerRole(player: Player) {
        removeScoreboardForPlayer(player)
        boards[player.uniqueId]?.updateLines(
            listOf("", "${ChatColor.RED}Role: ${ChatColor.WHITE}${game.getRoleFromPlayer(player)!!.name}", "")
        )
    }
}