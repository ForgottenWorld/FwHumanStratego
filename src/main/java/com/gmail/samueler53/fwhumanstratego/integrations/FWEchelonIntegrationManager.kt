package com.gmail.samueler53.fwhumanstratego.integrations

import com.gmail.samueler53.fwhumanstratego.configuration.Configuration
import com.gmail.samueler53.fwhumanstratego.objects.Game
import it.forgottenworld.echelonapi.FWEchelon
import it.forgottenworld.echelonapi.FWEchelonApi
import org.bukkit.Bukkit

object FWEchelonIntegrationManager {

    private val isEchelonPresent get() = Bukkit
        .getPluginManager()
        .getPlugin("FWEchelon") != null

    var useEchelon = false

    private val minigame by lazy {
        HumanStrategoMinigame()
    }


    fun enableIntegration() {
        if (!Configuration.enableFWEchelonIntegration) return
        val logger = Bukkit.getLogger()
        logger.info("FWEchelon integration is enabled")

        if (!isEchelonPresent) {
            logger.info("FWEchelon is not present")
            return
        }

        useEchelon = true
        logger.info("FWEchelon is present")
        val api = FWEchelon.api
        hookOntoMutexService(api)
        hookOntoMinigameService(api)
    }

    private fun hookOntoMutexService(api: FWEchelonApi) {
        api.mutexActivityService.registerMutexActivity(HumanStrategoMutexActivity())
    }

    private fun hookOntoMinigameService(api: FWEchelonApi) {
        api.minigameService.registerMinigameForRotation(minigame)
    }

    fun onMinigameFinished(game: Game) {
        if (!useEchelon || game !== minigame.game) return
        FWEchelon.api.minigameService.onFinish(minigame)
        minigame.game = null
    }

}