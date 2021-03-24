package com.gmail.samueler53.fwhumanstratego.managers

import com.gmail.samueler53.fwhumanstratego.FwHumanStratego
import com.gmail.samueler53.fwhumanstratego.gui.SetArenaGui
import com.gmail.samueler53.fwhumanstratego.message.Message
import com.gmail.samueler53.fwhumanstratego.objects.Arena
import com.gmail.samueler53.fwhumanstratego.objects.Game
import com.gmail.samueler53.fwhumanstratego.objects.Team
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.Chest
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object ArenaManager {

    fun createArena(name: String, player: Player) {
        if (FwHumanStratego.data.arene.none { it.name.equals(name, ignoreCase = true) }) {
            FwHumanStratego.data.arene.add(Arena(name))
            Message.ARENA_CREATED.send(player, name)
            FwHumanStratego.data.arene = FwHumanStratego.data.arene
        } else {
            Message.ARENA_ALREADY_EXISTS.send(player, name)
        }
    }

    private fun removeChest(location: Location) {
        if (location.block.type == Material.CHEST) {
            clearChest(location.block)
            location.block.type = Material.AIR
        }
    }

    private fun clearChest(block: Block) {
        val chest = block.state as Chest
        chest.blockInventory.clear()
    }

    fun deleteArena(arena: Arena, player: Player) {
        removeChest(arena.redTeamLocation!!)
        removeChest(arena.blueTeamLocation!!)
        FwHumanStratego.data.arene.remove(arena)
        Message.ARENA_REMOVE.send(player)
        FwHumanStratego.data.arene = FwHumanStratego.data.arene
    }

    fun teleportTeams(arena: Arena, game: Game) {
        teleportRedTeam(arena, game)
        teleportBlueTeam(arena, game)
    }

    private fun teleportRedTeam(arena: Arena, game: Game) {
        for (uuid in game.redTeam.playersRoles.keys) {
            Bukkit.getPlayer(uuid)?.teleport(arena.redTeamLocation!!)
        }
    }

    private fun teleportRedPlayer(player: Player, game: Game) {
        player.teleport(game.arena.redTeamLocation!!)
    }

    private fun teleportBlueTeam(arena: Arena, game: Game) {
        for (uuid in game.blueTeam.playersRoles.keys) {
            Bukkit.getPlayer(uuid)?.teleport(arena.blueTeamLocation!!)
        }
    }

    private fun teleportBluePlayer(player: Player, game: Game) {
        player.teleport(game.arena.blueTeamLocation!!)
    }

    fun teleportPlayerToLobby(player: Player, arena: Arena) {
        player.teleport(arena.lobbyLocation!!)
    }

    fun teleportPlayerToHisSpawnPoint(player: Player, game: Game) {
        if (game.getTeamFromPlayer(player).type == Team.Type.RED) {
            teleportRedPlayer(player, game)
        } else {
            teleportBluePlayer(player, game)
        }
    }

    fun initializeArena(arena: Arena) {
        treasureRed(arena.treasureRedLocation!!, arena)
        treasureBlue(arena.treasureBlueLocation!!, arena)
    }

    fun treasureRed(location: Location, arena: Arena) {
        removeChest(arena.treasureRedLocation!!)
        location.block.type = Material.CHEST
        val block = location.block
        if (block.type != Material.CHEST) {
            return
        }
        val chest = block.state as Chest
        chest.blockInventory.addItem(ItemStack(Material.RED_WOOL))
    }

    fun treasureBlue(location: Location, arena: Arena) {
        removeChest(arena.treasureBlueLocation!!)
        location.block.type = Material.CHEST
        val block = location.block
        if (block.type != Material.CHEST) {
            return
        }
        val chest = block.state as Chest
        chest.blockInventory.addItem(ItemStack(Material.BLUE_WOOL))
    }

    fun areLocationsSet(arena: Arena): Boolean {
        return arena.redTeamLocation != null &&
            arena.blueTeamLocation != null &&
            arena.treasureRedLocation != null &&
            arena.treasureBlueLocation != null &&
            arena.lobbyLocation != null
    }

    fun setTreasureRedLocation(
        treasureRedLocation: Location,
        arena: Arena,
        player: Player
    ) {
        treasureRed(treasureRedLocation, arena)

        val x = treasureRedLocation.blockX
        val y = treasureRedLocation.blockY
        val z = treasureRedLocation.blockZ
        val loc = Location(treasureRedLocation.world, x.toDouble(), y.toDouble(), z.toDouble())

        arena.treasureRedLocation = loc
        Message.ARENA_CREATION_TREASURERED.send(player)
        player.closeInventory()
    }

    fun setTreasureBlueLocation(
        treasureBlueLocation: Location,
        arena: Arena,
        player: Player
    ) {
        treasureBlue(treasureBlueLocation, arena)

        val x = treasureBlueLocation.blockX
        val y = treasureBlueLocation.blockY
        val z = treasureBlueLocation.blockZ
        val loc = Location(treasureBlueLocation.world, x.toDouble(), y.toDouble(), z.toDouble())

        arena.treasureBlueLocation = loc

        Message.ARENA_CREATION_TREASUREBLUE.send(player)
        FwHumanStratego.data.arene = FwHumanStratego.data.arene
        player.closeInventory()
    }

    fun setRedTeamLocation(
        redTeamLocation: Location,
        arena: Arena,
        player: Player
    ) {
        arena.redTeamLocation = redTeamLocation
        Message.ARENA_CREATION_TEAMRED.send(player)
        player.closeInventory()
    }

    fun setBlueTeamLocation(
        blueTeamLocation: Location,
        arena: Arena,
        player: Player
    ) {
        arena.blueTeamLocation = blueTeamLocation
        Message.ARENA_CREATION_TEAMBLUE.send(player)
        player.closeInventory()
    }

    fun setLobbyLocation(
        lobbyLocation: Location,
        arena: Arena,
        player: Player
    ) {
        arena.lobbyLocation = lobbyLocation
        Message.ARENA_CREATION_LOBBY.send(player)
        player.closeInventory()
    }

    fun setArena(player: Player, arena: Arena) {
        SetArenaGui.showToPlayer(player, arena)
    }

    fun findArenaByName(arenaName: String) = FwHumanStratego.data.arene.find { it.name == arenaName }

    fun sendArenaListToPlayer(player: Player) {
        for (arena in FwHumanStratego.data.arene) {
            Message.ARENA_LIST.send(player, arena.name)
        }
    }
}