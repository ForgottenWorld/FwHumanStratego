package com.gmail.samueler53.fwhumanstratego.objects

import com.gmail.samueler53.fwhumanstratego.gui.ArenaBuilderGui
import com.gmail.samueler53.fwhumanstratego.utils.GameplayUtils
import com.gmail.samueler53.fwhumanstratego.utils.WeakLocation
import kotlinx.serialization.Serializable

@Serializable
data class Arena(
    val name: String,
    val redSpawnWeakLocation: WeakLocation,
    val blueSpawnWeakLocation: WeakLocation,
    val treasureRedWeakLocation: WeakLocation,
    val treasureBlueWeakLocation: WeakLocation,
    val lobbyWeakLocation: WeakLocation
) {

    val redSpawnLocation get() = redSpawnWeakLocation.toLocation()

    val blueSpawnLocation get() = blueSpawnWeakLocation.toLocation()

    val lobbyLocation get() = lobbyWeakLocation.toLocation()

    fun ensureChestsExist() {
        GameplayUtils.ensureChestExistsAtLocation(redSpawnWeakLocation.toLocation())
        GameplayUtils.ensureChestExistsAtLocation(blueSpawnWeakLocation.toLocation())
    }

    class Builder(private val name: String) {

        val gui = ArenaBuilderGui.newInstance()

        var redTeamWeakLocation: WeakLocation? = null

        var blueTeamWeakLocation: WeakLocation? = null

        var treasureRedWeakLocation: WeakLocation? = null

        var treasureBlueWeakLocation: WeakLocation? = null

        var lobbyWeakLocation: WeakLocation? = null
        
        fun build(): Arena? {
            return Arena(
                name,
                redTeamWeakLocation ?: return null,
                blueTeamWeakLocation ?: return null,
                treasureRedWeakLocation ?: return null,
                treasureBlueWeakLocation ?: return null,
                lobbyWeakLocation ?: return null
            )
        }
    }
}