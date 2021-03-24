package com.gmail.samueler53.fwhumanstratego.objects

import com.gmail.samueler53.fwhumanstratego.FwHumanStratego
import com.gmail.samueler53.fwhumanstratego.gui.TeamGui
import com.gmail.samueler53.fwhumanstratego.managers.ArenaManager
import com.gmail.samueler53.fwhumanstratego.managers.GameManager
import com.gmail.samueler53.fwhumanstratego.message.Message
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*

class Game(
    val arena: Arena,
    var numberOfPlayers: Int
) {

    val playersPlaying = mutableListOf<UUID>()

    private val roles = mutableListOf<Role>()

    val playersLocations = mutableMapOf<UUID, Location>()

    var isStarted = false

    private var rounds = 0

    val redTeam = Team(Team.Type.RED, this)

    val blueTeam = Team(Team.Type.BLUE, this)

    val scoreboard = Scoreboard(this)

    init {
        loadDefaultRoles()
        loadRolesRemaining()
    }

    val teamGui = TeamGui(this)

    private fun loadDefaultRoles() {
        val config = FwHumanStratego.defaultConfig
        val rolesByName = mutableMapOf<String, Role>()
        for (k in config.getConfigurationSection("roles")!!.getKeys(false)) {
            val points = config.getInt("roles.$k.points")
            val description = config.getString("roles.$k.description")!!
            val maxPlayers = config.getInt("roles.$k.max_players")
            val canKillString = config.getString("roles.$k.can_kill")!!
            val roleName = k.toLowerCase()
            val role = Role(roleName, points, description, canKillString, maxPlayers)
            rolesByName[roleName] = role
            roles.add(role)
        }
        for (role in rolesByName.values) {
            role.canKill = role
                .canKillString
                .split(",")
                .map { rolesByName[it]!! }
                .toSet()
        }
    }

    fun loadRolesRemaining() {
        val rolesRemaining = roles.associateWith { 0 }.toMutableMap()
        val specialRolesList = roles.filter { isASpecialRole(it) }
        var specialRoles = numberOfPlayers / 2 - 1
        for (role in specialRolesList) {
            if (role.name.equals("generale", ignoreCase = true)) {
                rolesRemaining[role] = 1
                break
            }
        }
        while (specialRoles > 0) {
            for (role in specialRolesList) {
                if (specialRoles > 0 && !role.name.equals("generale", ignoreCase = true)) {
                    rolesRemaining[role] = rolesRemaining[role]!! + 1
                    specialRoles--
                }
            }
        }
        val normalRolesList = loadNormalRoles()
        var normalroles = numberOfPlayers
        while (normalroles > 0) {
            for (role in normalRolesList) {
                if (normalroles > 0) {
                    rolesRemaining[role] = rolesRemaining[role]!! + 1
                    normalroles--
                }
            }
        }
        redTeam.rolesRemaining = rolesRemaining
        blueTeam.rolesRemaining = rolesRemaining
    }

    private fun loadNormalRoles(): List<Role> {
        val normalRoles = mutableListOf<Role>()
        for (role in roles) {
            if (isANormalRole(role)) {
                normalRoles.add(role)
            }
        }
        return normalRoles
    }

    fun messageRole(player: Player) {
        val clickMessage = TextComponent("[INFORMAZIONI SUL RUOLO]")
        clickMessage.color = ChatColor.GREEN
        clickMessage.clickEvent = ClickEvent(
            ClickEvent.Action.RUN_COMMAND,
            """/hs info ${getRoleFromPlayer(player)!!.name}"""
        )
        clickMessage.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text("Clicca per avere informazioni sul tuo ruolo")
        )
        player.spigot().sendMessage(clickMessage)
    }

    fun addPlayer(player: Player) {
        if (!isPlayerPlaying(player)) {
            playersPlaying.add(player.uniqueId)
        }
    }

    fun removePlayer(player: Player) {
        if (isPlayerPlaying(player)) {
            playersPlaying.remove(player.uniqueId)
        }
    }

    fun teamAssignment(player: Player, team: Team) {
        team.addPlayer(player)
    }

    fun start() {
        Bukkit.getScheduler().runTaskLater(
            FwHumanStratego.plugin,
            Runnable {
                isStarted = true
                rounds++
                initializeRoleGui()
                survivalModeEachPlayer()
                ArenaManager.teleportTeams(arena, this)
                equipKits()
                scoreboard.initScoreboards()
                ArenaManager.initializeArena(arena)
                GameManager.gamesGui.removeGame(this)
            },
            FwHumanStratego.defaultConfig.getInt("delayStartGame").toLong()
        )
    }

    private fun equipKits() {
        redTeam.equipKitForEachPlayer()
        blueTeam.equipKitForEachPlayer()
    }

    fun initializeRoleGui() {
        redTeam.roleGui.addItemStack()
        blueTeam.roleGui.addItemStack()
    }

    fun endRound(team: Team?) {
        Message.GAME_ROUNDWINNER.broadcast(this, team!!.type)
        scoreboard.removeScoreboards()
        clearEachPlayer()
        clearRoles()
        if (isEndGame()) {
            endGame()
        } else {
            spectatorModeEachPlayer()
            loadRolesRemaining()
            Message.GAME_NEWROUND.broadcast(this)
            start()
        }
    }

    private fun endGame() {
        isStarted = false
        if (isDraw()) {
            Message.GAME_DRAW.broadcast(this)
        } else {
            val teamWinner = getTeamWinner()
            Message.GAME_TEAMWINNER.broadcast(this, teamWinner.type)
        }
        survivalModeEachPlayer()
        teleportPlayersInPreviouslyLocation()
        GameManager.removeGame(this)
    }

    fun teleportPlayersInPreviouslyLocation() {
        for (uuid in playersLocations.keys) {
            Bukkit.getPlayer(uuid)?.teleport(playersLocations[uuid]!!)
        }
    }

    fun addPlayerRole(
        player: Player,
        role: Role,
        team: Team
    ) {
        team.addPlayerRole(player, role)
    }

    private fun clearRoles() {
        redTeam.clearRoles()
        blueTeam.clearRoles()
    }

    fun clearEachPlayer() {
        playersPlaying
            .mapNotNull(Bukkit::getPlayer)
            .forEach(::clearPlayer)
    }

    private fun survivalModeEachPlayer() {
        playersPlaying
            .mapNotNull(Bukkit::getPlayer)
            .forEach(::survivalMode)
    }

    private fun spectatorModeEachPlayer() {
        for (uuid in playersPlaying) {
            val player = Bukkit.getPlayer(uuid) ?: return
            player.gameMode = GameMode.SPECTATOR
        }
    }

    fun clearPlayer(player: Player) {
        player.foodLevel = 20
        for (effect in player.activePotionEffects) {
            player.removePotionEffect(effect.type)
        }
        survivalMode(player)
        if (player.walkSpeed != 0.2f) {
            player.walkSpeed = 0.2f
        }
        player.inventory.clear()
        player.inventory.setArmorContents(null)
    }

    private fun survivalMode(player: Player) {
        if (player.gameMode != GameMode.SURVIVAL) {
            player.gameMode = GameMode.SURVIVAL
        }
    }

    fun stolenWool(player: Player) {
        val otherTeam = getOtherTeam(getTeamFromPlayer(player))
        if (otherTeam.treasure == Material.BLUE_WOOL) {
            player.inventory.remove(Material.BLUE_WOOL)
            ArenaManager.treasureBlue(arena.treasureBlueLocation!!, arena)
        } else if (otherTeam.treasure == Material.RED_WOOL) {
            player.inventory.remove(Material.RED_WOOL)
            ArenaManager.treasureRed(arena.treasureRedLocation!!, arena)
        }
    }

    fun spectateMode(team: Team) {
        for (uuid in team.playersRoles.keys) {
            val player = Bukkit.getPlayer(uuid) ?: continue
            if (hasPlayerRole(player)) continue
            player.closeInventory()
            player.gameMode = GameMode.SPECTATOR
        }
    }

    private fun canKill(roleDamageDealer: Role, roleDamaged: Role) =
        roleDamageDealer.canKill.contains(roleDamaged)

    fun areInTheSameTeam(
        player1: Player,
        player2: Player
    ) = getTeamFromPlayer(player1) == getTeamFromPlayer(player2)

    fun isPlayerInTeam(player: Player) = redTeam.playersRoles.containsKey(player.uniqueId) ||
        blueTeam.playersRoles.containsKey(player.uniqueId)

    private fun hasPlayerRole(player: Player) = getTeamFromPlayer(player).playersRoles[player.uniqueId] != null

    fun hasStoleWool(player: Player): Boolean {
        val otherTeam = getOtherTeam(getTeamFromPlayer(player))
        return player.inventory.contains(otherTeam.treasure)
    }

    fun isPlayerPlaying(player: Player) = playersPlaying.contains(player.uniqueId)

    fun isReadyToStart() = numberOfPlayers / 2 == redTeam.playersRoles.size &&
        numberOfPlayers / 2 == blueTeam.playersRoles.size

    private fun isASpecialRole(role: Role): Boolean {
        return role.name.equals("generale", ignoreCase = true) ||
            role.name.equals("assassino", ignoreCase = true) ||
            role.name.equals("bomba", ignoreCase = true) ||
            role.name.equals("artificiere", ignoreCase = true)
    }

    private fun isANormalRole(role: Role): Boolean {
        return role.name.equals("maresciallo", ignoreCase = true) ||
            role.name.equals("colonnello", ignoreCase = true) ||
            role.name.equals("maggiore", ignoreCase = true)
    }

    fun onPlayerLeave(player: Player) {
        if (isPlayerInTeam(player)) {
            val team = getTeamFromPlayer(player)
            if (isStarted) {
                Message.GAME_DESERTER.broadcast(this, player.displayName)
                if (hasPlayerRole(player)) {
                    val role = getRoleFromPlayer(player)!!
                    team.rolesRemaining[role] = team.rolesRemaining[role]!! + 1
                    team.playersRoles.remove(player.uniqueId)
                    team.roleGui.updateGui()
                    if (hasStoleWool(player)) {
                        stolenWool(player)
                        Message.GAME_TREASURESAVED.broadcast(this, player.displayName)
                    }
                }
            }
            team.removePlayer(player)
            teamGui.updateGui()
        }
        clearPlayer(player)
        removePlayer(player)
        player.teleport(playersLocations[player.uniqueId]!!)
        playersLocations.remove(player.uniqueId)
    }

    fun isRoleAvailableForTeam(role: Role, team: Team) = getPlayerWhoHaveThisRole(role, team) < role.maxPlayers

    fun isRemainingARole(role: Role?, team: Team) = team.rolesRemaining[role]!! > 0

    fun isGeneral(player: Player) = getRoleFromPlayer(player)
        ?.name
        .equals("Generale", ignoreCase = true)

    fun isBomb(player: Player): Boolean {
        return getRoleFromPlayer(player)?.name.equals("Bomba", ignoreCase = true)
    }

    private fun isEndGame() = rounds == FwHumanStratego.defaultConfig.getInt("rounds")

    fun isSpectateMode(team: Team) = team.rolesRemaining.values.none { it != 0 }

    private fun isDraw() = redTeam.points == blueTeam.points

    fun getOtherTeam(team: Team) = if (team == redTeam) {
        blueTeam
    } else {
        redTeam
    }

    fun getRoleFromPlayer(player: Player) = getTeamFromPlayer(player).playersRoles[player.uniqueId]

    fun getRoleByName(roleName: String) = roles.find { it.name.equals(roleName, ignoreCase = true) }

    fun getPlayerWhoHaveThisRole(
        role: Role,
        team: Team
    ) = team.playersRoles.count { (_, v) ->
        v == role
    }

    fun getTeamFromPlayer(player: Player) = if (redTeam.playersRoles.containsKey(player.uniqueId)) redTeam else blueTeam

    fun getWhoLose(damageDealer: Player, damaged: Player): Player {
        val roleDamageDealer = getRoleFromPlayer(damageDealer)!!
        val roleDamaged = getRoleFromPlayer(damaged)!!
        return if (canKill(roleDamageDealer, roleDamaged)) damaged else damageDealer
    }

    fun getWhoWin(damageDealer: Player, damaged: Player): Player {
        val roleDamageDealer = getRoleFromPlayer(damageDealer)!!
        val roleDamaged = getRoleFromPlayer(damaged)!!
        return if (canKill(roleDamageDealer, roleDamaged)) damageDealer else damaged
    }

    private fun getTeamWinner() = if (redTeam.points > blueTeam.points) redTeam else blueTeam
}