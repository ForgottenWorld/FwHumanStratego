package com.gmail.samueler53.fwhumanstratego.commands

import com.gmail.samueler53.fwhumanstratego.managers.GameManager
import com.gmail.samueler53.fwhumanstratego.managers.RoleManager
import com.gmail.samueler53.fwhumanstratego.message.Message
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class UserCommand : TabExecutor {

    private val commands = listOf(
        "join",
        "team",
        "leave",
        "role",
        "info"
    )

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (sender !is Player) return true

        if (!sender.hasPermission("humanstratego.usercommand")) return true

        val game by lazy { GameManager.getGameForPlayer(sender) }

        when (args.getOrNull(0)?.toLowerCase()) {
            "join" -> {
                if (game != null) {
                    Message.GAME_LEAVE_GAME_FIRST.send(sender)
                } else {
                    GameManager.gamesGui.show(sender)
                }
            }
            "team" -> {
                game?.showTeamGui(sender)
            }
            "leave" -> {
                game?.onPlayerLeave(sender, false)
            }
            "role" -> {
                game?.onPlayerRequestRoleChange(sender)
            }
            "info" -> {
                if (args.size == 2) {
                    RoleManager.getRoleByName(args[1])?.let {
                        sender.sendMessage(it.description)
                    }
                }
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