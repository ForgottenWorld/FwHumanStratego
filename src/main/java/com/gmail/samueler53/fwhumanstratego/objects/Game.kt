package com.gmail.samueler53.fwhumanstratego.objects

import com.gmail.samueler53.fwhumanstratego.configuration.Configuration
import com.gmail.samueler53.fwhumanstratego.gui.RoleGui
import com.gmail.samueler53.fwhumanstratego.gui.TeamGui
import com.gmail.samueler53.fwhumanstratego.managers.ArenaManager
import com.gmail.samueler53.fwhumanstratego.managers.GameManager
import com.gmail.samueler53.fwhumanstratego.managers.RoleManager
import com.gmail.samueler53.fwhumanstratego.message.Message
import com.gmail.samueler53.fwhumanstratego.utils.GameplayUtils
import com.gmail.samueler53.fwhumanstratego.utils.delayTicks
import com.gmail.samueler53.fwhumanstratego.utils.editItemMetaOfType
import com.gmail.samueler53.fwhumanstratego.utils.itemStack
import com.gmail.samueler53.fwhumanstratego.utils.launch
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import java.util.*

class Game(
    val arena: Arena,
    var numberOfPlayers: Int
) {

    val playersPlaying = mutableSetOf<UUID>()

    val teamGui = TeamGui.newInstance(this)

    private val playerLocations = mutableMapOf<UUID, Location>()

    private val scoreboard = Scoreboard(this)

    var currentRound = 0

    val redTeam = object : Team(
        "ROSSO",
        Material.RED_WOOL,
        getColoredLeatherChestplate(Color.RED),
        Message.GAME_ASSIGNED_TEAM_RED
    ) {
        override val roleGui = RoleGui.newInstance(this@Game, this)
        override val spawnLocation get() = arena.redTeamLocation
    }

    val blueTeam = object : Team(
        "BLU",
        Material.BLUE_WOOL,
        getColoredLeatherChestplate(Color.BLUE),
        Message.GAME_ASSIGNED_TEAM_BLUE
    ) {
        override val roleGui = RoleGui.newInstance(this@Game, this)
        override val spawnLocation get() = arena.blueTeamLocation
    }

    init {
        resetAvailableRoles()
    }

    private fun resetAvailableRoles() {
        val playersPerTeam = numberOfPlayers / 2
        val available = RoleManager.roles.values.filter {
            it.minPlayersToActivate <= playersPerTeam
        }
        val mandatory = available.filter { it.minPlayers > 0 }
        val rolesRemaining = available.associateWith { r ->
            val capped = r.maxPlayers.coerceAtMost(playersPerTeam)
            val reserved = mandatory.filterNot { it == r }.sumBy { it.minPlayers }
            capped - reserved
        }
        redTeam.rolesRemaining = rolesRemaining.toMutableMap()
        blueTeam.rolesRemaining = rolesRemaining.toMutableMap()
    }

    fun onPlayerJoin(player: Player) {
        if (numberOfPlayers == playersPlaying.size) {
            Message.GAME_GAMEFULL.send(player)
            return
        }

        Message.GAME_JOIN.send(player)

        playersPlaying.add(player.uniqueId)

        GameManager.setGameForPlayer(player, this)
        GameManager.gamesGui.updateGameInfo(this)
        playerLocations[player.uniqueId] = player.location
        player.teleport(arena.lobbyLocation)
        GameplayUtils.cleansePlayer(player)
        Message.GAME_CHOOSE_TEAM.send(player)
        launch {
            delayTicks(2)
            teamGui.gui.show(player)
        }
    }

    private fun onDelayRoundStart() {
        launch {
            delayTicks(Configuration.delayGameStart)
            onRoundStart()
        }
    }

    private fun onRoundStart() {
        ++currentRound
        redTeam.roleGui.updateRoles()
        blueTeam.roleGui.updateRoles()
        redTeam.preparePlayersForRound()
        blueTeam.preparePlayersForRound()
        scoreboard.initScoreboards()
        ArenaManager.initializeArena(arena)
        GameManager.gamesGui.onGameRemoved(this@Game)
    }

    private fun onRoundEnd(winningTeam: Team) {
        Message.GAME_ROUND_WINNER.broadcast(this, winningTeam.name)
        scoreboard.removeScoreboards()
        cleanseAllPlayers()
        redTeam.playersRoles.clear()
        blueTeam.playersRoles.clear()

        if (currentRound == Configuration.rounds) {
            onGameEnd()
            return
        }

        for (player in playersPlaying.mapNotNull(Bukkit::getPlayer)) {
            player.gameMode = GameMode.SPECTATOR
        }

        resetAvailableRoles()
        Message.GAME_NEW_ROUND.broadcast(this)
        onDelayRoundStart()
    }

    private fun onGameEnd() {
        if (redTeam.score == blueTeam.score) {
            Message.GAME_DRAW.broadcast(this)
        } else {
            val teamWinner = if (redTeam.score > blueTeam.score) redTeam else blueTeam
            Message.GAME_GAME_OVER_TEAM_WINS.broadcast(this, teamWinner.name)
        }
        putAllPlayersInSurvivalMode()
        teleportPlayersInPreviouslyLocation()
        GameManager.removeGame(arena)
    }

    private fun teleportPlayersInPreviouslyLocation() {
        for (uuid in playerLocations.keys) {
            Bukkit.getPlayer(uuid)?.teleport(playerLocations[uuid]!!)
        }
    }

    private fun cleanseAllPlayers() {
        playersPlaying
            .mapNotNull(Bukkit::getPlayer)
            .forEach(GameplayUtils::cleansePlayer)
    }

    private fun putAllPlayersInSurvivalMode() {
        playersPlaying
            .mapNotNull(Bukkit::getPlayer)
            .forEach {
                it.gameMode = GameMode.SURVIVAL
            }
    }

    fun onPlayerChangeNumberOfPlayers(player: Player, newNumberOfPlayers: Int) {
        if (currentRound > 0) {
            Message.GAME_STARTED.send(player)
            return
        }

        if (newNumberOfPlayers % 2 != 0 || newNumberOfPlayers <= 1) {
            Message.GAME_ODD_PLAYERS.send(player)
            return
        }

        if (playersPlaying.size > newNumberOfPlayers) {
            Message.GAME_UNMODIFIABLE.send(player)
            return
        }

        if (redTeam.playersRoles.size + blueTeam.playersRoles.size == numberOfPlayers) {
            Message.GAME_UNMODIFIABLE_2.send(player)
            return
        }

        Message.GAME_EDITABLE.send(player)
        numberOfPlayers = newNumberOfPlayers
        resetAvailableRoles()
        teamGui.update()
        GameManager.gamesGui.updateGameInfo(this)

        if (isReadyToStart) {
            Message.GAME_IS_STARTING.broadcast(this)
            onDelayRoundStart()
        }
    }

    fun onPlayerStopGame(player: Player) {
        teleportPlayersInPreviouslyLocation()
        cleanseAllPlayers()
        scoreboard.removeScoreboards()
        GameManager.gamesGui.onGameRemoved(this)
        GameManager.removeGame(arena)
        Message.GAME_STOPPED.send(player)
    }

    private fun onPlayerLoseWool(player: Player) {
        val otherTeam = player.team.opponent
        if (!player.inventory.contains(otherTeam.treasure)) return
        player.inventory.remove(otherTeam.treasure)
        if (otherTeam.treasure == Material.BLUE_WOOL) {
            ArenaManager.setBlueTreasure(arena.treasureBlueLocation, arena)
        } else if (otherTeam.treasure == Material.RED_WOOL) {
            ArenaManager.setRedTreasure(arena.treasureRedLocation, arena)
        }
        Message.GAME_TREASURE_SAVED.broadcast(this, player.displayName)
    }

    fun onPlayerChangeRole(player: Player, role: Role) {
        val team = player.team

        if (team.countPlayersWithRole(role) >= role.maxPlayers) {
            Message.GAME_ROLE_FULL.send(player)
            return
        }

        if (team.rolesRemaining[role]!! <= 0) {
            Message.GAME_ROLE_NO_LONGER_AVAIL.send(player)
            return
        }

        team.setPlayerRole(player, role)

        if (team.hasRolesLeft) return

        team.players
            .mapNotNull(Bukkit::getPlayer)
            .forEach {
                it.closeInventory()
                it.gameMode = GameMode.SPECTATOR
            }
    }

    private fun isPlayerInTeam(player: Player) = redTeam.players.contains(player.uniqueId) ||
        blueTeam.players.contains(player.uniqueId)

    fun containsPlayer(player: Player) = playersPlaying.contains(player.uniqueId)

    private val isReadyToStart
        get() = numberOfPlayers / 2 == redTeam.players.size &&
            numberOfPlayers / 2 == blueTeam.players.size

    fun onPlayerAttackPlayer(attacker: Player, attacked: Player) {
        if (currentRound == 0) {
            Message.GAME_PREPARATION_PHASE.send(attacker)
            return
        }

        val attackerTeam = attacker.team
        val attackedTeam = attacked.team

        if (attackerTeam == attackedTeam) {
            Message.GAME_SAME_TEAM.send(attacker)
            return
        }

        val attackerRole = attacker.role ?: return
        val attackedRole = attacked.role ?: return

        if (attackedRole == attackerRole) return

        if (!attackerRole.canAttack) {
            Message.THIS_ROLE_CANT_ATTACK.send(attacker)
            return
        }

        val loser: Player
        val winner: Player
        val losingRole: Role
        val losingTeam: Team

        when {
            attackerRole.canKill(attackedRole) -> {
                winner = attacker
                loser = attacked
                losingRole = attackedRole
                losingTeam = attackedTeam
            }
            attackedRole.counterattacks && attackedRole.canKill(attackerRole) -> {
                winner = attacked
                loser = attacker
                losingRole = attackerRole
                losingTeam = attackerTeam
            }
            else -> return
        }

        winner.playSound(winner.location, Sound.ENTITY_PLAYER_ATTACK_CRIT, 10.0f, 10.0f)

        losingTeam.opponent.score += losingRole.points
        scoreboard.removeScoreboardForPlayer(loser)
        losingTeam.teleportPlayerToSpawn(loser)

        if (losingRole.isVital) {
            Message.GAME_VITAL_CHAR_DEAD.broadcast(this)
            onRoundEnd(losingTeam.opponent)
            return
        }

        onPlayerLoseWool(loser)

        losingTeam.playersRoles.remove(loser.uniqueId)
        losingTeam.roleGui.updateRoles()
    }

    fun onPlayerLeave(player: Player, disconnect: Boolean) {
        if (!disconnect && currentRound > 0) {
            Message.GAME_LEAVE_WHEN_STARTED.send(player)
            return
        }

        if (isPlayerInTeam(player)) {
            player.team.removePlayer(player)
        }

        GameManager.removePlayerFromGame(player)

        GameplayUtils.cleansePlayer(player)

        if (!disconnect) {
            Message.GAME_LEAVE.send(player)
            GameManager.gamesGui.updateGameInfo(this)
        }

        playersPlaying.remove(player.uniqueId)
        player.teleport(playerLocations[player.uniqueId]!!)
        playerLocations.remove(player.uniqueId)
    }

    fun onPlayerRequestRoleChange(player: Player) {
        if (currentRound == 0 || player.role != null) return
        player.team.roleGui.gui.show(player)
    }

    private val Player.role get() = this.team.playersRoles[uniqueId]

    private val Player.team get() = if (redTeam.players.contains(uniqueId)) redTeam else blueTeam

    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        if (!isPlayerInTeam(event.player)) return
        event.isCancelled = true
        val team = event.player.team
        val message = when (team) {
            redTeam -> "§4Red team %s: %s"
            blueTeam -> "§9Blue team %s: %s"
            else -> return
        }
        for (p in team.playersRoles.keys.mapNotNull(Bukkit::getPlayer)) {
            p.sendMessage(message.format(event.player.displayName, event.message))
        }
    }

    fun onInventoryClick(player: Player, event: InventoryClickEvent) {
        if (event.slotType == InventoryType.SlotType.ARMOR) {
            Message.GAME_CANT_TAKE_ARMOR_OFF.send(player)
            event.isCancelled = true
            return
        }

        if (player.gameMode == GameMode.SPECTATOR) {
            event.isCancelled = true
            return
        }

        if (currentRound == 0) return
        val team = player.team

        with(event) {
            when {
                currentItem?.type == team.treasure -> {
                    Message.GAME_CANT_STILL_OWN_WOOL.send(player)
                    isCancelled = true
                }
                clickedInventory!!.location == arena.treasureBlueLocation && team == redTeam -> {
                    if (currentItem!!.type == team.opponent.treasure) {
                        Message.GAME_STOLEN_WOOL_BLUE.broadcast(this@Game, player.displayName)
                    }
                }
                clickedInventory!!.location == arena.treasureRedLocation && team == blueTeam -> {
                    if (currentItem!!.type == team.opponent.treasure) {
                        Message.GAME_STOLEN_WOOL_RED.broadcast(this@Game, player.displayName)
                    }
                }
                view.topInventory.location != null &&
                    currentItem != null &&
                    (currentItem!!.type == Material.RED_WOOL || currentItem!!.type == Material.BLUE_WOOL) &&
                    view.topInventory.location != arena.treasureRedLocation &&
                    view.topInventory.location != arena.treasureBlueLocation -> {
                    isCancelled = true
                }
            }

            if (inventory.holder == arena.treasureRedLocation && team == redTeam ||
                inventory.location == arena.treasureBlueLocation && team == blueTeam
            ) {
                if (inventory.contains(team.opponent.treasure)) {
                    launch {
                        delayTicks(1)
                        Message.GAME_TREASURE_STOLEN.broadcast(this@Game)
                        team.score += Configuration.pointsFromTreasure
                        onRoundEnd(team)
                    }
                }
            }
        }
    }

    fun sendArenaInfoToPlayer(player: Player) {
        player.sendMessage("§aNome partita: ${arena.name}")
        player.sendMessage("§aPlayer attuali: ${playersPlaying.size}")
        player.sendMessage("§aPlayer massimi: $numberOfPlayers")
    }

    fun onPlayerChooseTeam(player: Player, team: Team) {
        if (team.playersRoles.size == numberOfPlayers / 2) {
            Message.GAME_TEAMFULL.send(player)
            return
        }

        if (team.playersRoles.containsKey(player.uniqueId)) {
            team.removePlayer(player)
        } else {
            val otherTeam = team.opponent
            if (otherTeam.playersRoles.containsKey(player.uniqueId)) {
                otherTeam.removePlayer(player)
            }
        }

        team.players.add(player.uniqueId)
        teamGui.update()
        player.closeInventory()

        team.teamAssignedMessage.send(player)

        if (isReadyToStart) {
            Message.GAME_IS_STARTING.broadcast(this)
            onDelayRoundStart()
        }
    }

    private fun getColoredLeatherChestplate(color: Color) =
        Material.LEATHER_CHESTPLATE.itemStack {
            editItemMetaOfType<LeatherArmorMeta> {
                setColor(color)
            }
        }

    abstract inner class Team(
        val name: String,
        val treasure: Material,
        private val kit: ItemStack,
        val teamAssignedMessage: Message
    ) {

        abstract val roleGui: RoleGui

        abstract val spawnLocation: Location

        var score = 0

        val players = mutableListOf<UUID>()

        var playersRoles = mutableMapOf<UUID, Role>()

        var rolesRemaining = mutableMapOf<Role, Int>()
            set(value) {
                field = value
                roleGui.updateRoles()
            }

        val hasRolesLeft get() = rolesRemaining.values.none { it != 0 }

        val opponent: Team get() = if (redTeam == this) this else blueTeam

        fun setPlayerRole(player: Player, role: Role) {
            playersRoles[player.uniqueId] = role
            rolesRemaining[role] = rolesRemaining[role]!! - 1
            scoreboard.updatePlayerRole(player, role)
            roleGui.updateRoles()
            player.sendTitle("Il tuo nuovo ruolo e'", role.name, 30, 100, 30)
            GameplayUtils.cleansePlayer(player, false)
            role.sendInfoLinkToPlayer(player)
        }

        fun teleportPlayerToSpawn(player: Player) {
            player.teleport(spawnLocation)
            launch {
                Message.GAME_CHOOSE_ROLE.send(player)
                delayTicks(2)
                if (!hasRolesLeft) {
                    player.gameMode = GameMode.SPECTATOR
                } else {
                    roleGui.gui.show(player)
                }
                GameplayUtils.rootPlayer(player)
            }
        }

        fun preparePlayersForRound() {
            for (player in players.mapNotNull(Bukkit::getPlayer)) {
                player.gameMode = GameMode.SURVIVAL
                player.inventory.chestplate = kit
                teleportPlayerToSpawn(player)
            }
        }

        fun removePlayer(player: Player) {
            players.remove(player.uniqueId)

            if (currentRound == 0) {
                playersRoles.remove(player.uniqueId)
                teamGui.update()
                return
            }

            Message.GAME_DESERTER.broadcast(this@Game, player.displayName)
            val role = player.role
            playersRoles.remove(player.uniqueId)
            teamGui.update()

            if (role == null) return
            rolesRemaining[role] = rolesRemaining[role]!! + 1
            roleGui.updateRoles()

            onPlayerLoseWool(player)
        }

        fun countPlayersWithRole(role: Role) = playersRoles.values.count { it == role }
    }
}