package com.gmail.samueler53.fwhumanstratego.objects

import com.gmail.samueler53.fwhumanstratego.gui.ArenaBuilderGui
import com.gmail.samueler53.fwhumanstratego.utils.WeakLocation
import kotlinx.serialization.Serializable

@Serializable
data class Arena(
    val name: String,
    private val redTeamWeakLocation: WeakLocation,
    private val blueTeamWeakLocation: WeakLocation,
    private val treasureRedWeakLocation: WeakLocation,
    private val treasureBlueWeakLocation: WeakLocation,
    private val lobbyWeakLocation: WeakLocation
) {

    val redTeamLocation get() = redTeamWeakLocation.toLocation()

    val blueTeamLocation get() = blueTeamWeakLocation.toLocation()

    val treasureRedLocation get() = treasureRedWeakLocation.toLocation()

    val treasureBlueLocation get() = treasureBlueWeakLocation.toLocation()

    val lobbyLocation get() = lobbyWeakLocation.toLocation()
    
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