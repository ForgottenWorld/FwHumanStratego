package com.gmail.samueler53.fwhumanstratego.commands

import com.gmail.samueler53.fwhumanstratego.configuration.Configuration
import com.gmail.samueler53.fwhumanstratego.managers.ArenaManager
import com.gmail.samueler53.fwhumanstratego.managers.GameManager
import com.gmail.samueler53.fwhumanstratego.message.Message
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class AdminCommand : TabExecutor {

    private val commands = listOf(
        "arenas",
        "create",
        "discard",
        "info",
        "modify",
        "reload",
        "remove",
        "set",
        "start",
        "stop",
    )

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (sender !is Player) return true

        if (!sender.hasPermission("humanstratego.admincommand")) return true

        val arena by lazy {
            if (args.size < 2) return@lazy null
            ArenaManager.getArenaByName(args[1]) ?: run {
                Message.ARENA_NOT_FOUND.send(sender)
                null
            }
        }

        val game by lazy {
            arena?.let(GameManager::getGameForArena) ?: run {
                Message.GAME_ARENA_FREE.send(sender)
                null
            }
        }

        val numberOfPlayers by lazy {
            if (args.size < 3) return@lazy null
            args[2].toIntOrNull() ?: run {
                Message.GAME_VALUE.send(sender)
                null
            }
        }

        when (args.getOrNull(0)?.toLowerCase()) {
            "arenas" -> {
                ArenaManager.sendArenaListToPlayer(sender)
            }
            "reload" -> {
                Configuration.reload()
                Message.GAME_RELOAD.send(sender)
            }
            "create" -> {
                if (args.size < 2) return false
                ArenaManager.onPlayerStartBuildingArena(sender, args[1])
            }
            "stop" -> {
                game?.onGameStopped(sender)
            }
            "remove" -> {
                arena?.let { ArenaManager.onPlayerDeleteArena(sender, it) }
            }
            "set" -> {
                ArenaManager.getArenaBuilderForPlayer(sender)?.gui?.show(sender)
            }
            "discard" -> {
                ArenaManager.onPlayerStopBuildingArena(sender)
            }
            "info" -> {
                game?.onPlayerRequestInfo(sender) ?: Message.GAME_ARENA_FREE.send(sender)
            }
            "start" -> {
                if (numberOfPlayers == null) return false
                arena?.let { GameManager.onPlayerStartNewGame(sender, it, numberOfPlayers!!) }
            }
            "modify" -> {
                if (numberOfPlayers == null) return false
                game?.onPlayerChangeNumberOfPlayers(sender, numberOfPlayers!!)
            }
            else -> return false
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): List<String> {
        if (args.size != 1) return listOf()
        return commands.filter { it.startsWith(args[0], ignoreCase = true) }
    }
}