package com.gmail.samueler53.fwhumanstratego.objects

import com.gmail.samueler53.fwhumanstratego.message.Message
import fr.mrmicky.fastboard.FastBoard
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

class Scoreboard internal constructor() {

    private val boards = mutableMapOf<UUID, FastBoard>()

    fun initScoreboards(players: List<Player>) {
        for (player in players) {
            boards[player.uniqueId] = FastBoard(player).apply {
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

    fun updatePlayerRole(player: Player, role: Role) {
        removeScoreboardForPlayer(player)
        boards[player.uniqueId]?.updateLines(
            listOf("", "§cRole: ${ChatColor.WHITE}${role.name}", "")
        )
    }
}