package com.gmail.samueler53.fwhumanstratego.utils

import kotlinx.serialization.Serializable
import org.bukkit.Bukkit

@Serializable
class LocationSerializable(
    private val worldUUID: String,
    val x: Int,
    val y: Int,
    val z: Int
) {

    val world get() = Bukkit.getWorld(worldUUID)
}