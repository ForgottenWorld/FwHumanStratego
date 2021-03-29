package com.gmail.samueler53.fwhumanstratego.objects

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
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
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.inventory.ItemStack
import java.util.*

class Game(
    val arena: Arena,
    var numberOfPlayers: Int
) {

    private val players = mutableSetOf<UUID>()

    private val teamGui = TeamGui.newInstance(this)

    private val playerReturnLocations = mutableMapOf<UUID, WeakLocation>()
    private val playerReturnGameModes = mutableMapOf<UUID, GameMode>()

    private val scoreboard = Scoreboard()

    private var currentRound = 0

    private val redTeam = object : Team(
        "ROSSO",
        Material.RED_WOOL,
        arena.redTreasureLocation,
        GameplayUtils.createDyedLeatherChestplate(Color.RED),
        Message.GAME_ASSIGNED_TEAM_RED,
        Message.GAME_STOLEN_WOOL_RED
    ) {
        override val roleGui = RoleGui.newInstance(this@Game, this)
        override val spawnLocation get() = arena.redSpawnLocation.toLocation()
    }

    private val blueTeam = object : Team(
        "BLU",
        Material.BLUE_WOOL,
        arena.blueTreasureLocation,
        GameplayUtils.createDyedLeatherChestplate(Color.BLUE),
        Message.GAME_ASSIGNED_TEAM_BLUE,
        Message.GAME_STOLEN_WOOL_BLUE
    ) {
        override val roleGui = RoleGui.newInstance(this@Game, this)
        override val spawnLocation get() = arena.blueSpawnLocation.toLocation()
    }


    val currentNumberOfPlayers get() = players.size

    private fun resetAvailableRoles() {
        val playersPerTeam = numberOfPlayers / 2
        val available = RoleManager.roles.values.filter {
            it.minPlayersToActivate <= playersPerTeam
        }
        val rolesRemaining = available.associateWith { it.uses }
        redTeam.rolesRemaining = rolesRemaining.toMutableMap()
        blueTeam.rolesRemaining = rolesRemaining.toMutableMap()
    }

    fun onPlayerJoin(player: Player) {
        if (numberOfPlayers == currentNumberOfPlayers) {
            Message.GAME_IS_FULL.send(player)
            return
        }

        Message.GAME_JOINED_SUCCESS.send(player)

        playerReturnLocations[player.uniqueId] = WeakLocation.ofLocation(player.location)
        playerReturnGameModes[player.uniqueId] = player.gameMode
        players.add(player.uniqueId)
        GameManager.setGameForPlayer(player, this)

        GameplayUtils.cleansePlayer(player)

        player.teleport(arena.lobbyLocation.toLocation())

        launch {
            delayTicks(1)
            Message.GAME_CHOOSE_TEAM.send(player)
            teamGui.show(player)
        }
    }

    private fun startGameIfReady() {
        val playersPerTeam = numberOfPlayers / 2
        if (redTeam.players.size != playersPerTeam ||
            blueTeam.players.size != playersPerTeam
        ) return
        Message.GAME_IS_STARTING.broadcast()
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

        scoreboard.initScoreboards(players.mapNotNull(Bukkit::getPlayer))
    }

    private fun onRoundEnd(winningTeam: Team?) {
        scoreboard.removeScoreboards()
        redTeam.playersRoles.clear()
        blueTeam.playersRoles.clear()

        if (winningTeam == null) return

        Message.GAME_ROUND_WINNER.broadcast(winningTeam.name)

        for (player in players.mapNotNull(Bukkit::getPlayer)) {
            GameplayUtils.cleansePlayer(player)
            player.gameMode = GameMode.SPECTATOR
        }

        if (currentRound == Configuration.rounds) {
            onGameEnd()
            return
        }

        Message.GAME_NEW_ROUND.broadcast()
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
            player.gameMode = playerReturnGameModes[player.uniqueId]!!
            player.teleport(loc.toLocation())
        }
        GameManager.removeGame(arena)
        if (stopped) return
        arrayOf(redTeam, blueTeam)
            .maxByOrNull { it.score }
            ?.let { Message.GAME_GAME_OVER_TEAM_WINS.broadcast(it.name) }
            ?: Message.GAME_DRAW.broadcast()
    }

    private fun onPlayerLoseWool(player: Player) {
        val otherTeam = player.team.opponent
        if (!player.inventory.contains(otherTeam.treasure)) return
        player.inventory.remove(otherTeam.treasure)
        otherTeam.restoreTreasure()
        Message.GAME_TREASURE_SAVED.broadcast(player.displayName)
    }

    fun onInventoryOpen(event: InventoryOpenEvent) {
        if (event.inventory.holder is ChestGui ||
            event.inventory.holder === redTeam.treasureChest ||
            event.inventory.holder === blueTeam.treasureChest
        ) return
        event.isCancelled = true
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
        teamGui.update(redTeam.players.size, blueTeam.players.size)
        GameManager.gamesGui.update()

        startGameIfReady()
    }

    fun onPlayerChangeRole(player: Player, role: Role): Boolean {
        val team = player.team

        if (team.countPlayersWithRole(role) >= role.maxPlayers) {
            Message.GAME_ROLE_FULL.send(player)
            return false
        }

        if (team.rolesRemaining[role]!! == 0) {
            Message.GAME_ROLE_NO_LONGER_AVAIL.send(player)
            return false
        }

        team.setPlayerRole(player, role)
        return true
    }

    fun onPlayerAttackPlayer(attacker: Player, attacked: Player) {
        if (currentRound == 0) {
            Message.GAME_PREPARATION_PHASE.send(attacker)
            return
        }

        val attackerTeam = attacker.team
        val attackedTeam = attacked.team

        if (attackerTeam === attackedTeam) {
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

        val winner: Player
        val loser: Player
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

        if (losingRole.isVital) {
            Message.GAME_VITAL_CHAR_DEAD.broadcast()
            onRoundEnd(losingTeam.opponent)
            return
        }

        losingTeam.respawnPlayer(loser)
        losingTeam.playersRoles.remove(loser.uniqueId)
        losingTeam.roleGui.updateRoles()
    }

    fun onPlayerLeave(player: Player, disconnect: Boolean) {
        if (!disconnect && currentRound > 0) {
            Message.GAME_LEAVE_WHEN_STARTED.send(player)
            return
        }

        player.team.removePlayer(player)

        GameManager.removePlayerFromGame(player)

        GameplayUtils.cleansePlayer(player)

        if (!disconnect) Message.GAME_LEAVE.send(player)

        players.remove(player.uniqueId)
        player.gameMode = playerReturnGameModes[player.uniqueId]!!
        player.teleport(playerReturnLocations[player.uniqueId]!!.toLocation())
        playerReturnLocations.remove(player.uniqueId)
        playerReturnGameModes.remove(player.uniqueId)
    }

    fun onPlayerRequestRoleChange(player: Player) {
        if (currentRound == 0 || player.role != null && !Configuration.allowRoleChanges) {
            Message.GAME_CANT_CHANGE_ROLE_NOW.send(player)
            return
        }
        player.team.roleGui.show(player)
    }

    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        if (!redTeam.players.contains(event.player.uniqueId) &&
            !blueTeam.players.contains(event.player.uniqueId)
        ) return
        event.isCancelled = true
        val team = event.player.team
        val message = when (team) {
            redTeam -> "§4Red team %s: %s"
            blueTeam -> "§9Blue team %s: %s"
            else -> return
        }
        for (p in team.players.mapNotNull(Bukkit::getPlayer)) {
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

        if (item.type == team.treasure && event.inventory === teamTreasureChest) {
            Message.GAME_CANT_STEAL_OWN_WOOL.send(player)
            event.isCancelled = true
            return
        }

        val opposingTeam = team.opponent

        if (item.type != opposingTeam.treasure) return

        if (event.inventory == opposingTeam.treasureChest) {
            opposingTeam.woolStolenMessage.broadcast(player.displayName)
            return
        }

        if (event.inventory == teamTreasureChest && event.inventory.contains(item)) {
            Message.GAME_TREASURE_STOLEN.broadcast()
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

    fun showTeamGui(player: Player) {
        if (currentRound > 0) return
        teamGui.show(player)
    }

    fun onPlayerChooseTeam(player: Player, isRedTeam: Boolean) {
        val team = if (isRedTeam) redTeam else blueTeam
        if (team.players.contains(player.uniqueId)) return

        if (team.playersRoles.size == numberOfPlayers / 2) {
            Message.GAME_TEAMFULL.send(player)
            return
        }

        team.opponent.removePlayer(player)

        team.players.add(player.uniqueId)
        teamGui.update(redTeam.players.size, blueTeam.players.size)
        player.closeInventory()

        team.teamAssignedMessage.send(player)

        startGameIfReady()
    }

    private val Player.role get() = this.team.playersRoles[uniqueId]

    private val Player.team get() = if (redTeam.players.contains(uniqueId)) redTeam else blueTeam

    private fun Message.broadcast(vararg objects: Any) {
        players.mapNotNull(Bukkit::getPlayer).forEach {
            send(it, *objects)
        }
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

        val opponent get() = if (redTeam == this) this else blueTeam

        val treasureChest get() = treasureLocation.block?.state as? Chest


        fun restoreTreasure() {
            val tr = treasureChest ?: return
            tr.inventory.clear()
            tr.inventory.addItem(treasure.itemStack())
        }

        fun setPlayerRole(player: Player, role: Role) {
            GameplayUtils.cleansePlayer(player, false)
            playersRoles[player.uniqueId] = role
            rolesRemaining[role] = rolesRemaining[role]!! - 1
            scoreboard.updatePlayerRole(player, role)
            roleGui.updateRoles()
            player.sendTitle("Il tuo nuovo ruolo e'", role.name, 30, 100, 30)
            role.sendInfoLinkToPlayer(player)
        }

        fun respawnPlayer(player: Player) {
            player.teleport(spawnLocation)
            launch {
                delayTicks(1)
                if (!rolesRemaining.values.none { it != 0 }) {
                    player.gameMode = GameMode.SPECTATOR
                } else {
                    Message.GAME_CHOOSE_ROLE.send(player)
                    roleGui.show(player)
                    GameplayUtils.rootPlayer(player)
                }
            }
        }

        fun prepareForRound() {
            roleGui.updateRoles()
            restoreTreasure()
            for (player in players.mapNotNull(Bukkit::getPlayer)) {
                player.gameMode = GameMode.ADVENTURE
                player.inventory.chestplate = kit
                respawnPlayer(player)
            }
        }

        fun removePlayer(player: Player) {
            if (!players.contains(player.uniqueId)) return

            players.remove(player.uniqueId)

            if (currentRound == 0) {
                playersRoles.remove(player.uniqueId)
                teamGui.update(redTeam.players.size, blueTeam.players.size)
                return
            }

            Message.GAME_DESERTER.broadcast(player.displayName)

            if (players.size <= 2) {
                score = -1
                onGameEnd()
                return
            }

            playersRoles.remove(player.uniqueId)
            roleGui.updateRoles()

            onPlayerLoseWool(player)
        }

        fun countPlayersWithRole(role: Role) = playersRoles.values.count(role::equals)
    }
}