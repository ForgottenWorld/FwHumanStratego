package com.gmail.samueler53.fwhumanstratego.objects

data class Role(
    val name: String,
    val points: Int,
    val description: String,
    val canKillString: String,
    val maxPlayers: Int
) {
    lateinit var canKill: Set<Role>
}