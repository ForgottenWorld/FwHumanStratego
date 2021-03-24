package com.gmail.samueler53.fwhumanstratego.commands

import com.gmail.samueler53.fwhumanstratego.FwHumanStratego
import com.gmail.samueler53.fwhumanstratego.managers.ArenaManager
import com.gmail.samueler53.fwhumanstratego.managers.GameManager
import com.gmail.samueler53.fwhumanstratego.message.Message
import com.gmail.samueler53.fwhumanstratego.objects.Arena
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class AdminCommand(private val plugin: FwHumanStratego) : CommandExecutor, TabExecutor {

    private val commandRegistry = listOf(
        "stop",
        "create",
        "remove",
        "set",
        "start",
        "modify",
        "info",
        "arenas",
        "reload"
    )

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (sender !is Player) return true
        if (!sender.hasPermission("humanstratego.admincommand")) return true
        if (args.size == 1) {
            when (args[0]) {
                "arenas" -> {
                    ArenaManager.sendArenaListToPlayer(sender)
                }
                "reload" -> {
                    plugin.reloadDefaultConfig()
                    Message.GAME_RELOAD.send(sender)
                }
            }
            return true
        }

        if (args.size < 2) return true

        if (args[0].equals("create", ignoreCase = true)) {
            ArenaManager.createArena(args[1], sender)
            return true
        }

        if (ArenaManager.findArenaByName(args[1]) == null) {
            Message.ARENA_NOT_FOUND.send(sender)
            return true
        }

        val arena = ArenaManager.findArenaByName(args[1]) ?: return true

        when (args[0]) {
            "stop" -> {
                stopGame(arena, sender)
            }
            "remove" -> {
                ArenaManager.deleteArena(arena, sender)
            }
            "set" -> {
                ArenaManager.setArena(sender, arena)
            }
            "info" -> {
                info(arena, sender)
            }
        }

        if (args.size == 3) {
            when (args[0]) {
                "start" -> {
                    createGame(args[2], arena, sender)
                }
                "modify" -> {
                    modifyGame(args[2], arena, sender)
                }
            }
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): List<String> {
        val lastArg: String
        val suggestions: List<String>
        when (args.size) {
            1 -> {
                lastArg = args[0]
                suggestions = commandRegistry
            }
            2 -> {
                lastArg = args[1]
                suggestions = listOf()
            }
            else -> return listOf()
        }
        return suggestions.filter { it.startsWith(lastArg, ignoreCase = true) }
    }

    private fun createGame(
        value: String,
        arena: Arena,
        player: Player
    ) {
        if (GameManager.gamesGui.getNumberOfGamesInTheGui() >= 9) {
            Message.GAME_NOMOREGAMES.send(player)
            return
        }
        
        val numberOfPlayers = value.toIntOrNull() ?: run {
            Message.GAME_VALUE.send(player)
            return
        }
        
        if (numberOfPlayers % 2 != 0 || numberOfPlayers <= 1) {
            Message.GAME_ODDPLAYERS.send(player)
            return
        }
        
        if (!ArenaManager.areLocationsSet(arena)) {
            Message.GAME_SETPOINTS.send(player)
            return
        }
        
        if (GameManager.isArenaBusy(arena)) {
            Message.GAME_ARENABUSY.send(player)
            return
        }
        
        val game = GameManager.startNewGame(arena, numberOfPlayers)
        GameManager.message()
        GameManager.gamesGui.createNewGame(arena, game)
    }

    private fun stopGame(arena: Arena, player: Player) {
        val game = GameManager.getGameForArena(arena) ?: run {
            Message.GAME_ARENAFREE.send(player)
            return
        }
        with (game) {
            teleportPlayersInPreviouslyLocation()
            clearEachPlayer()
            scoreboard.removeScoreboards()
            GameManager.gamesGui.removeGame(this)
            GameManager.removeGame(this)
        }
        Message.GAME_STOPPED.send(player)
    }

    private fun modifyGame(value: String, arena: Arena, player: Player) {
        val game = GameManager.getGameForArena(arena) ?: run {
            Message.GAME_ARENAFREE.send(player)
            return
        }

        val numberOfPlayers = value.toIntOrNull() ?: run {
            Message.GAME_VALUE.send(player)
            return
        }
        
        if (game.isStarted) {
            Message.GAME_STARTED.send(player)
            return
        }
        
        if (numberOfPlayers % 2 != 0 || numberOfPlayers <= 1) {
            Message.GAME_ODDPLAYERS.send(player)
            return
        }
        
        if (game.playersPlaying.size > numberOfPlayers) {
            Message.GAME_UNMODIFIABLE.send(player)
            return
        }
        
        if (game.redTeam.playersRoles.size + game.blueTeam.playersRoles.size == game.numberOfPlayers) {
            Message.GAME_UNMODIFIABLE2.send(player)
            return
        }
        
        Message.GAME_EDITABLE.send(player)
        game.numberOfPlayers = numberOfPlayers
        game.loadRolesRemaining()
        game.teamGui.updateGui()
        game.initializeRoleGui()
        GameManager.gamesGui.modifyGame(game)
        
        if (game.isReadyToStart()) {
            Message.GAME_ISSTARTING.broadcast(game)
            game.start()
        }
    }

    private fun info(arena: Arena, player: Player) {
        val game = GameManager.getGameForArena(arena) ?: run {
            Message.GAME_ARENAFREE.send(player)
            return
        }
        player.sendMessage("${ChatColor.GREEN}Nome partita: ${arena.name}")
        player.sendMessage("${ChatColor.GREEN}Player attuali: ${game.playersPlaying.size}")
        player.sendMessage("${ChatColor.GREEN}Player massimi: ${game.numberOfPlayers}")
    }
}