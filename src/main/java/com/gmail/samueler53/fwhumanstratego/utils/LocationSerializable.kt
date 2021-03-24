package com.gmail.samueler53.fwhumanstratego.utils

import org.bukkit.Bukkit
import org.bukkit.World
import java.io.Serializable

class LocationSerializable(
    world: World,
    val x: Int,
    val y: Int,
    val z: Int
) : Serializable {
    private val world = world.uid

    fun getWorld() = Bukkit.getWorld(world)

    companion object {
        @Transient
        private const val serialVersionUID = 1681012206529286330L
    }
}