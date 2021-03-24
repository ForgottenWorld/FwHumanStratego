package com.gmail.samueler53.fwhumanstratego.objects

import com.gmail.samueler53.fwhumanstratego.utils.LocationSerializable
import org.bukkit.Location
import java.io.Serializable

class Arena(val name: String) : Serializable {

    private var redTeamLocationSerializable: LocationSerializable? = null
    private var blueTeamLocationSerializable: LocationSerializable? = null
    private var treasureRedLocationSerializable: LocationSerializable? = null
    private var treasureBlueLocationSerializable: LocationSerializable? = null
    private var lobbyLocationSerializable: LocationSerializable? = null

    var redTeamLocation
        get() = redTeamLocationSerializable?.let {
            Location(
                it.getWorld(),
                it.x.toDouble(),
                it.y.toDouble(),
                it.z.toDouble()
            )
        }
        set(value) {
            this.redTeamLocationSerializable = LocationSerializable(
                value!!.world!!,
                value.blockX,
                value.blockY,
                value.blockZ
            )
        }

    var blueTeamLocation
        get() = blueTeamLocationSerializable?.let {
            Location(
                it.getWorld(),
                it.x.toDouble(),
                it.y.toDouble(),
                it.z.toDouble()
            )
        }
        set(value) {
            blueTeamLocationSerializable = LocationSerializable(
                value!!.world!!,
                value.blockX,
                value.blockY,
                value.blockZ
            )
        }

    var treasureRedLocation
        get() = treasureRedLocationSerializable?.let {
            Location(
                it.getWorld(),
                it.x.toDouble(),
                it.y.toDouble(),
                it.z.toDouble()
            )
        }
        set(value) {
            this.treasureRedLocationSerializable = LocationSerializable(
                value!!.world!!,
                value.blockX,
                value.blockY,
                value.blockZ
            )
        }

    var treasureBlueLocation
        get() = treasureBlueLocationSerializable?.let {
            Location(
                it.getWorld(),
                it.x.toDouble(),
                it.y.toDouble(),
                it.z.toDouble()
            )
        }
        set(value) {
            treasureBlueLocationSerializable = LocationSerializable(
                value!!.world!!,
                value.blockX,
                value.blockY,
                value.blockZ
            )
        }

    var lobbyLocation
        get() = lobbyLocationSerializable?.let {
            Location(
                it.getWorld(),
                it.x.toDouble(),
                it.y.toDouble(),
                it.z.toDouble()
            )
        }
        set(value) {
            this.lobbyLocationSerializable = LocationSerializable(
                value!!.world!!,
                value.blockX,
                value.blockY,
                value.blockZ
            )
        }

    companion object {
        @Transient
        private val serialVersionUID = 1681012206529286330L
    }
}