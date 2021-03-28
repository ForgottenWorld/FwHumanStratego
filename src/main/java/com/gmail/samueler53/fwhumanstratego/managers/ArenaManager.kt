package com.gmail.samueler53.fwhumanstratego.managers

import com.charleskorn.kaml.Yaml
import com.gmail.samueler53.fwhumanstratego.FwHumanStratego
import com.gmail.samueler53.fwhumanstratego.message.Message
import com.gmail.samueler53.fwhumanstratego.objects.Arena
import com.gmail.samueler53.fwhumanstratego.utils.WeakLocation
import com.gmail.samueler53.fwhumanstratego.utils.launchAsync
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import org.bukkit.entity.Player
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
        if (!ARENA_SAVE_FILE.exists()) {
            ARENA_SAVE_FILE.createNewFile()
            return
        }

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

    fun onPlayerStartBuildingArena(player: Player, name: String) {
        if (arenas.keys.contains(name)) {
            Message.ARENA_ALREADY_EXISTS.send(player, name)
            return
        }

        val builder = Arena.Builder(name)
        arenaBuilders[player.uniqueId] = builder
        Message.ARENA_BUILDER_CREATED.send(player)
        builder.gui.show(player)
    }

    fun onPlayerStopBuildingArena(player: Player) {
        arenaBuilders.remove(player.uniqueId)
    }

    fun onPlayerDeleteArena(player: Player, arena: Arena) {
        arenas.remove(arena.name)
        Message.ARENA_REMOVE.send(player)
        saveData()
    }

    fun getArenaBuilderForPlayer(player: Player) = arenaBuilders[player.uniqueId].also {
        it ?: Message.ARENA_CREATION_NOT_EDITING.send(player)
    }

    private fun tryBuildingArena(player: Player, builder: Arena.Builder) {
        val arena = builder.build() ?: return
        arenas[arena.name] = arena
        Message.ARENA_CREATED.send(player, arena.name)
        saveData()
    }

    fun setTreasureRedLocation(player: Player) {
        val builder = getArenaBuilderForPlayer(player) ?: return
        builder.redTreasureLocation = WeakLocation.ofLocation(player.location)
        Message.ARENA_CREATION_TREASURE_RED.send(player)
        tryBuildingArena(player, builder)
    }

    fun setTreasureBlueLocation(player: Player) {
        val builder = getArenaBuilderForPlayer(player) ?: return
        builder.blueTreasureLocation = WeakLocation.ofLocation(player.location)
        Message.ARENA_CREATION_TREASURE_BLUE.send(player)
        tryBuildingArena(player, builder)
    }

    fun setRedTeamLocation(player: Player) {
        val builder = getArenaBuilderForPlayer(player) ?: return
        builder.redTeamLocation = WeakLocation.ofLocation(player.location)
        Message.ARENA_CREATION_TEAMRED.send(player)
        tryBuildingArena(player, builder)
    }

    fun setBlueTeamLocation(player: Player) {
        val builder = getArenaBuilderForPlayer(player) ?: return
        builder.blueTeamLocation = WeakLocation.ofLocation(player.location)
        Message.ARENA_CREATION_TEAMBLUE.send(player)
        tryBuildingArena(player, builder)
    }

    fun setLobbyLocation(player: Player) {
        val builder = getArenaBuilderForPlayer(player) ?: return
        builder.lobbyLocation = WeakLocation.ofLocation(player.location)
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