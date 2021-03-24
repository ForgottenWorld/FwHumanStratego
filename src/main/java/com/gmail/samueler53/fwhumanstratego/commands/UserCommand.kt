package com.gmail.samueler53.fwhumanstratego.commands

import com.gmail.samueler53.fwhumanstratego.managers.GameManager
import com.gmail.samueler53.fwhumanstratego.message.Message
import com.gmail.samueler53.fwhumanstratego.objects.Game
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class UserCommand : CommandExecutor {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (sender !is Player) return true
        if (!sender.hasPermission("humanstratego.usercommand")) return true
        when (args.size) {
            1 -> {
                when (args[0].toLowerCase()) {
                    "join" -> {
                        if (GameManager.getGameForPlayer(sender) == null) {
                            onPlayerRequestGamesGui(sender)
                        } else {
                            Message.GAME_LEAVEGAMEFIRST.send(sender)
                        }
                    }
                    "leave" -> {
                        GameManager.getGameForPlayer(sender)?.let {
                            onPlayerLeaveGame(it, sender)
                        }
                    }
                    "role" -> {
                        GameManager.getGameForPlayer(sender)?.let {
                            onPlayerRequestRoleGui(it, sender)
                        }
                    }
                }
            }
            2 -> {
                when (args[0].toLowerCase()) {
                    "join" -> {
                        val game = GameManager.getGameForPlayer(sender)
                        if (game != null && args[1].equals("team", ignoreCase = true)) {
                            onPlayerRequestTeamsGui(game, sender)
                        }
                    }
                    "info" -> {
                        GameManager.getGameForPlayer(sender)?.let {
                            onPlayerRequestRoleInfo(it, sender, args[1])
                        }
                    }
                }
            }
        }
        return true
    }

    private fun onPlayerRequestGamesGui(player: Player) {
        GameManager.gamesGui.show(player)
    }

    private fun onPlayerLeaveGame(game: Game, player: Player) {
        if (game.isStarted) {
            Message.GAME_LEAVEWHENSTARTED.send(player)
            return
        }

        Message.GAME_LEAVE.send(player)

        if (game.isPlayerInTeam(player)) {
            game.getTeamFromPlayer(player).removePlayer(player)
            game.teamGui.updateGui()
        }

        game.removePlayer(player)
        GameManager.gamesGui.modifyGame(game)
        player.teleport(game.playersLocations[player.uniqueId]!!)
        game.playersLocations.remove(player.uniqueId)
    }

    private fun onPlayerRequestRoleGui(game: Game, player: Player) {
        if (game.isStarted && game.getRoleFromPlayer(player) == null) {
            game.getTeamFromPlayer(player).roleGui.show(player)
        }
    }

    private fun onPlayerRequestTeamsGui(game: Game, player: Player) {
        if (!game.isStarted) {
            game.teamGui.show(player)
        }
    }

    private fun onPlayerRequestRoleInfo(game: Game, player: Player, roleName: String) {
        if (!game.isStarted) return
        val role = game.getRoleByName(roleName)
        if (role != null) {
            player.sendMessage(role.description)
        }
    }
}