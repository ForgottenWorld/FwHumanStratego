package com.gmail.samueler53.fwhumanstratego.integrations

import com.gmail.samueler53.fwhumanstratego.managers.GameManager
import it.forgottenworld.echelonapi.mutexactivity.MutexActivity
import org.bukkit.entity.Player

class HumanStrategoMutexActivity : MutexActivity {

    override val id = MUTEX_ACTIVITY_NAME


    override fun onAllPlayersForceRemoved(reason: String?) {
        GameManager.onAllPlayersForceRemoved(reason)
    }

    override fun onPlayerForceRemoved(player: Player, reason: String?) {
        GameManager.onPlayerForceRemoved(player, reason)
    }

    companion object {
        private const val MUTEX_ACTIVITY_NAME = "FWHumanStratego"
    }
}