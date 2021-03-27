package com.gmail.samueler53.fwhumanstratego.utils

import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.Location

@Serializable
class WeakLocation(
    private val worldUUID: String,
    private val x: Int,
    private val y: Int,
    private val z: Int
) {

    fun toLocation() = Location(
        Bukkit.getWorld(worldUUID),
        x.toDouble(),
        y.toDouble(),
        z.toDouble()
    )

    companion object {

        fun ofLocation(location: Location) = WeakLocation(
            location.world!!.uid.toString(),
            location.blockX,
            location.blockY,
            location.blockZ
        )
    }

}