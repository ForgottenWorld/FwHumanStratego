package com.gmail.samueler53.fwhumanstratego.managers

import com.charleskorn.kaml.Yaml
import com.gmail.samueler53.fwhumanstratego.FwHumanStratego
import com.gmail.samueler53.fwhumanstratego.message.Message
import com.gmail.samueler53.fwhumanstratego.objects.Arena
import com.gmail.samueler53.fwhumanstratego.utils.WeakLocation
import com.gmail.samueler53.fwhumanstratego.utils.launchAsync
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File
import java.io.IOException
import java.util.*

object ArenaManager {

    private val ARENA_SAVE_FILE by lazy {
        File(FwHumanStratego.instance.dataFolder, "arenas.yml")
    }

    private var arenas = mutableMapOf<String, Arena>()

    private val arenaBuilders = mutableMapOf<UUID, Arena.Builder>()
    
    fun loadData() {
        val deserialized = Yaml.default.decodeFromString(
            MapSerializer(String.serializer(), Arena.serializer()),
            ARENA_SAVE_FILE.readText()
        )
        arenas = deserialized.toMutableMap()
    }

    private fun saveData() {
        val saveData = Yaml.default.encodeToString(
            MapSerializer(String.serializer(), Arena.serializer()),
            arenas
        )

        launchAsync {
            try {
                ARENA_SAVE_FILE.writeText(saveData)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun playerStartBuildingArena(player: Player, name: String) {
        if (arenas.keys.contains(name)) {
            Message.ARENA_ALREADY_EXISTS.send(player, name)
            return
        }

        arenaBuilders[player.uniqueId] = Arena.Builder(name)
        Message.ARENA_BUILDER_CREATED.send(player)
        saveData()
    }

    private fun removeChest(location: Location) {
        if (location.block.type != Material.CHEST) return
        (location.block.state as Chest).blockInventory.clear()
        location.block.type = Material.AIR
    }

    fun onPlayerDeleteArena(player: Player, arena: Arena) {
        removeChest(arena.redTeamLocation)
        removeChest(arena.blueTeamLocation)
        arenas.remove(arena.name)
        Message.ARENA_REMOVE.send(player)
        saveData()
    }

    fun initializeArena(arena: Arena) {
        setRedTreasure(arena.treasureRedLocation, arena)
        setBlueTreasure(arena.treasureBlueLocation, arena)
    }

    private fun setTreasure(
        location: Location,
        oldLocation: Location?,
        material: Material
    ) {
        oldLocation?.let(::removeChest)
        location.block.type = Material.CHEST
        val block = location.block
        if (block.type != Material.CHEST) return
        (block.state as Chest).blockInventory.addItem(ItemStack(material))
    }

    fun setRedTreasure(location: Location, arena: Arena) {
        setTreasure(location, arena.treasureRedLocation, Material.RED_WOOL)
    }

    fun setBlueTreasure(location: Location, arena: Arena) {
        setTreasure(location, arena.treasureBlueLocation, Material.BLUE_WOOL)
    }

    fun getPlayerArenaBuilder(player: Player) = arenaBuilders[player.uniqueId]
        ?: run {
            Message.ARENA_CREATION_NOT_EDITING.send(player)
            null
        }

    private fun tryBuildingArena(player: Player, builder: Arena.Builder) {
        val arena = builder.build() ?: return
        arenas[arena.name] = arena
        Message.ARENA_CREATED.send(player, arena.name)
    }

    fun setTreasureRedLocation(player: Player) {
        val builder = getPlayerArenaBuilder(player) ?: return
        builder.treasureRedWeakLocation = WeakLocation.ofLocation(player.location)
        Message.ARENA_CREATION_TREASURE_RED.send(player)
        tryBuildingArena(player, builder)
    }

    fun setTreasureBlueLocation(player: Player) {
        val builder = getPlayerArenaBuilder(player) ?: return
        builder.treasureBlueWeakLocation = WeakLocation.ofLocation(player.location)
        Message.ARENA_CREATION_TREASURE_BLUE.send(player)
        tryBuildingArena(player, builder)
    }

    fun setRedTeamLocation(player: Player) {
        val builder = getPlayerArenaBuilder(player) ?: return
        builder.redTeamWeakLocation = WeakLocation.ofLocation(player.location)
        Message.ARENA_CREATION_TEAMRED.send(player)
        tryBuildingArena(player, builder)
    }

    fun setBlueTeamLocation(player: Player) {
        val builder = getPlayerArenaBuilder(player) ?: return
        builder.blueTeamWeakLocation = WeakLocation.ofLocation(player.location)
        Message.ARENA_CREATION_TEAMBLUE.send(player)
        tryBuildingArena(player, builder)
    }

    fun setLobbyLocation(player: Player) {
        val builder = getPlayerArenaBuilder(player) ?: return
        builder.lobbyWeakLocation = WeakLocation.ofLocation(player.location)
        Message.ARENA_CREATION_LOBBY.send(player)
        tryBuildingArena(player, builder)
    }

    fun getArenaByName(arenaName: String) = arenas[arenaName.toLowerCase()]

    fun sendArenaListToPlayer(player: Player) {
        for (name in arenas.keys) {
            Message.ARENA_LIST.send(player, name)
        }
    }
}