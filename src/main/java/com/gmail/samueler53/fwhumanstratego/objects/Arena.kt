package com.gmail.samueler53.fwhumanstratego.objects

import com.gmail.samueler53.fwhumanstratego.gui.ArenaBuilderGui
import com.gmail.samueler53.fwhumanstratego.utils.GameplayUtils
import com.gmail.samueler53.fwhumanstratego.utils.WeakLocation
import kotlinx.serialization.Serializable

@Serializable
data class Arena(
    val name: String,
    val redSpawnLocation: WeakLocation,
    val blueSpawnLocation: WeakLocation,
    val redTreasureLocation: WeakLocation,
    val blueTreasureLocation: WeakLocation,
    val lobbyLocation: WeakLocation
) {

    fun ensureChestsExist() {
        GameplayUtils.ensureChestExistsAtLocation(redSpawnLocation.toLocation())
        GameplayUtils.ensureChestExistsAtLocation(blueSpawnLocation.toLocation())
    }

    class Builder(private val name: String) {

        val gui = ArenaBuilderGui.newInstance()

        var redTeamLocation: WeakLocation? = null

        var blueTeamLocation: WeakLocation? = null

        var redTreasureLocation: WeakLocation? = null

        var blueTreasureLocation: WeakLocation? = null

        var lobbyLocation: WeakLocation? = null
        
        fun build(): Arena? {
            return Arena(
                name,
                redTeamLocation ?: return null,
                blueTeamLocation ?: return null,
                redTreasureLocation ?: return null,
                blueTreasureLocation ?: return null,
                lobbyLocation ?: return null
            )
        }
    }
}