package com.gmail.samueler53.fwhumanstratego.objects

import com.gmail.samueler53.fwhumanstratego.utils.LocationSerializable
import kotlinx.serialization.Serializable
import org.bukkit.Location

@Serializable
data class Arena(
    val name: String,
    private var redTeamLocationSerializable: LocationSerializable? = null,
    private var blueTeamLocationSerializable: LocationSerializable? = null,
    private var treasureRedLocationSerializable: LocationSerializable? = null,
    private var treasureBlueLocationSerializable: LocationSerializable? = null,
    private var lobbyLocationSerializable: LocationSerializable? = null
) {

    var redTeamLocation
        get() = redTeamLocationSerializable?.let {
            Location(
                it.world,
                it.x.toDouble(),
                it.y.toDouble(),
                it.z.toDouble()
            )
        }
        set(value) {
            this.redTeamLocationSerializable = LocationSerializable(
                value!!.world!!.uid.toString(),
                value.blockX,
                value.blockY,
                value.blockZ
            )
        }

    var blueTeamLocation
        get() = blueTeamLocationSerializable?.let {
            Location(
                it.world,
                it.x.toDouble(),
                it.y.toDouble(),
                it.z.toDouble()
            )
        }
        set(value) {
            blueTeamLocationSerializable = LocationSerializable(
                value!!.world!!.uid.toString(),
                value.blockX,
                value.blockY,
                value.blockZ
            )
        }

    var treasureRedLocation
        get() = treasureRedLocationSerializable?.let {
            Location(
                it.world,
                it.x.toDouble(),
                it.y.toDouble(),
                it.z.toDouble()
            )
        }
        set(value) {
            this.treasureRedLocationSerializable = LocationSerializable(
                value!!.world!!.uid.toString(),
                value.blockX,
                value.blockY,
                value.blockZ
            )
        }

    var treasureBlueLocation
        get() = treasureBlueLocationSerializable?.let {
            Location(
                it.world,
                it.x.toDouble(),
                it.y.toDouble(),
                it.z.toDouble()
            )
        }
        set(value) {
            treasureBlueLocationSerializable = LocationSerializable(
                value!!.world!!.uid.toString(),
                value.blockX,
                value.blockY,
                value.blockZ
            )
        }

    var lobbyLocation
        get() = lobbyLocationSerializable?.let {
            Location(
                it.world,
                it.x.toDouble(),
                it.y.toDouble(),
                it.z.toDouble()
            )
        }
        set(value) {
            this.lobbyLocationSerializable = LocationSerializable(
                value!!.world!!.uid.toString(),
                value.blockX,
                value.blockY,
                value.blockZ
            )
        }
}