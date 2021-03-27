package com.gmail.samueler53.fwhumanstratego.objects

import com.gmail.samueler53.fwhumanstratego.configuration.Configuration
import com.gmail.samueler53.fwhumanstratego.gui.RoleGui
import com.gmail.samueler53.fwhumanstratego.gui.TeamGui
import com.gmail.samueler53.fwhumanstratego.managers.GameManager
import com.gmail.samueler53.fwhumanstratego.managers.RoleManager
import com.gmail.samueler53.fwhumanstratego.message.Message
import com.gmail.samueler53.fwhumanstratego.utils.GameplayUtils
import com.gmail.samueler53.fwhumanstratego.utils.WeakLocation
import com.gmail.samueler53.fwhumanstratego.utils.delayTicks
import com.gmail.samueler53.fwhumanstratego.utils.itemStack
import com.gmail.samueler53.fwhumanstratego.utils.launch
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Chest
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.inventory.ItemStack
import java.util.*

class Game(
    val arena: Arena,
    var numberOfPlayers: Int
) {

    val players = mutableSetOf<UUID>()

    val teamGui = TeamGui.newInstance(this)

    private val playerReturnLocations = mutableMapOf<UUID, WeakLocation>()

    private val scoreboard = Scoreboard(this)

    var currentRound = 0

    val redTeam = object : Team(
        "ROSSO",
        Material.RED_WOOL,
        arena.treasureRedWeakLocation,
        GameplayUtils.createDyedLeatherChestplate(Color.RED),
        Message.GAME_ASSIGNED_TEAM_RED,
        Message.GAME_STOLEN_WOOL_RED
    ) {
        override val roleGui = RoleGui.newInstance(this@Game, this)
        override val spawnLocation get() = arena.redSpawnLocation
    }

    val blueTeam = object : Team(
        "BLU",
        Material.BLUE_WOOL,
        arena.treasureBlueWeakLocation,
        GameplayUtils.createDyedLeatherChestplate(Color.BLUE),
        Message.GAME_ASSIGNED_TEAM_BLUE,
        Message.GAME_STOLEN_WOOL_BLUE
    ) {
        override val roleGui = RoleGui.newInstance(this@Game, this)
        override val spawnLocation get() = arena.blueSpawnLocation
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
        if (numberOfPlayers == players.size) {
            Message.GAME_IS_FULL.send(player)
            return
        }

        Message.GAME_JOINED_SUCCESS.send(player)

        playerReturnLocations[player.uniqueId] = WeakLocation.ofLocation(player.location)

        players.add(player.uniqueId)
        GameManager.setGameForPlayer(player, this)

        GameplayUtils.cleansePlayer(player)

        player.teleport(arena.lobbyLocation)

        Message.GAME_CHOOSE_TEAM.send(player)
        launch {
            delayTicks(1)
            teamGui.show(player)
        }
    }

    private fun startGameIfReady() {
        if (!isReadyToStart) return
        Message.GAME_IS_STARTING.broadcast(this)
        arena.ensureChestsExist()
        onDelayRoundStart()
    }

    private fun onDelayRoundStart() {
        launch {
            delayTicks(Configuration.delayGameStart)
            onRoundStart()
        }
    }

    private fun onRoundStart() {
        ++currentRound
        resetAvailableRoles()

        redTeam.prepareForRound()
        blueTeam.prepareForRound()

        scoreboard.initScoreboards()
    }

    private fun onRoundEnd(winningTeam: Team?) {
        scoreboard.removeScoreboards()
        redTeam.playersRoles.clear()
        blueTeam.playersRoles.clear()

        if (winningTeam == null) return

        Message.GAME_ROUND_WINNER.broadcast(this, winningTeam.name)

        for (player in players.mapNotNull(Bukkit::getPlayer)) {
            GameplayUtils.cleansePlayer(player)
            player.gameMode = GameMode.SPECTATOR
        }

        if (currentRound == Configuration.rounds) {
            onGameEnd()
            return
        }

        Message.GAME_NEW_ROUND.broadcast(this)
        onDelayRoundStart()
    }

    fun onPlayerStopGame(player: Player) {
        onRoundEnd(null)
        Message.GAME_STOPPED.send(player)
        onGameEnd(true)
    }

    private fun onGameEnd(stopped: Boolean = false) {
        for ((uuid, loc) in playerReturnLocations) {
            val player = Bukkit.getPlayer(uuid) ?: continue
            GameplayUtils.cleansePlayer(player)
            player.teleport(loc.toLocation())
        }
        GameManager.removeGame(arena)
        if (stopped) return
        arrayOf(redTeam, blueTeam)
            .maxByOrNull { it.score }
            ?.let { Message.GAME_GAME_OVER_TEAM_WINS.broadcast(this, it.name) }
            ?: Message.GAME_DRAW.broadcast(this)
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

        if (players.size > newNumberOfPlayers) {
            Message.GAME_UNMODIFIABLE.send(player)
            return
        }

        if (redTeam.playersRoles.size + blueTeam.playersRoles.size == numberOfPlayers) {
            Message.GAME_UNMODIFIABLE_2.send(player)
            return
        }

        Message.GAME_EDITED_SUCCESS.send(player)
        numberOfPlayers = newNumberOfPlayers
        teamGui.update()
        GameManager.gamesGui.update()

        startGameIfReady()
    }

    private fun onPlayerLoseWool(player: Player) {
        val otherTeam = player.team.opponent
        if (!player.inventory.contains(otherTeam.treasure)) return
        player.inventory.remove(otherTeam.treasure)
        otherTeam.restoreTreasure()
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
            GameManager.gamesGui.update()
        }

        players.remove(player.uniqueId)
        player.teleport(playerReturnLocations[player.uniqueId]!!.toLocation())
        playerReturnLocations.remove(player.uniqueId)
    }

    fun onPlayerRequestRoleChange(player: Player) {
        if (currentRound == 0 || player.role != null) return
        player.team.roleGui.show(player)
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
        val item = event.currentItem ?: return

        if (player.gameMode == GameMode.SPECTATOR) {
            event.isCancelled = true
            return
        }

        if (event.slotType == InventoryType.SlotType.ARMOR) {
            Message.GAME_CANT_TAKE_ARMOR_OFF.send(player)
            event.isCancelled = true
            return
        }

        if (currentRound == 0) return

        val team = player.team
        val teamTreasureChest = team.treasureChest

        if (item.type == team.treasure && event.inventory == teamTreasureChest) {
            Message.GAME_CANT_STEAL_OWN_WOOL.send(player)
            event.isCancelled = true
            return
        }

        val opposingTeam = team.opponent

        if (item.type != opposingTeam.treasure) return

        if (event.inventory == opposingTeam.treasureChest) {
            opposingTeam.woolStolenMessage.broadcast(this@Game, player.displayName)
            return
        }

        if (event.inventory == teamTreasureChest && event.inventory.contains(item)) {
            Message.GAME_TREASURE_STOLEN.broadcast(this@Game)
            team.score += Configuration.pointsFromTreasure
            onRoundEnd(team)
            return
        }
    }

    fun onPlayerRequestInfo(player: Player) {
        player.sendMessage("§aNome partita: ${arena.name}")
        player.sendMessage("§aPlayer attuali: ${players.size}")
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

        startGameIfReady()
    }

    abstract inner class Team(
        val name: String,
        val treasure: Material,
        private val treasureLocation: WeakLocation,
        private val kit: ItemStack,
        val teamAssignedMessage: Message,
        val woolStolenMessage: Message
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

        val opponent get() = if (redTeam == this) this else blueTeam

        val treasureChest get() = treasureLocation.block?.state as? Chest


        fun restoreTreasure() {
            val tr = treasureChest ?: return
            tr.inventory.clear()
            tr.inventory.addItem(treasure.itemStack())
        }

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
                    roleGui.show(player)
                }
                GameplayUtils.rootPlayer(player)
            }
        }

        fun prepareForRound() {
            roleGui.updateRoles()
            restoreTreasure()
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