package com.gmail.samueler53.fwhumanstratego.utils

import com.gmail.samueler53.fwhumanstratego.FwHumanStratego
import com.okkero.skedule.BukkitDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.plugin.java.JavaPlugin
import kotlin.coroutines.CoroutineContext

fun launch(f: suspend CoroutineScope.() -> Unit) = CoroutineScope(BukkitDispatchers.minecraft).launch(block = f)

fun launchAsync(f: suspend CoroutineScope.() -> Unit) = CoroutineScope(BukkitDispatchers.async).launch(block = f)

object BukkitDispatchers {
    val minecraft: CoroutineContext by lazy {
        BukkitDispatcher(
            JavaPlugin.getPlugin(FwHumanStratego::class.java)
        )
    }

    val async: CoroutineContext by lazy {
        BukkitDispatcher(
            JavaPlugin.getPlugin(FwHumanStratego::class.java),
            true
        )
    }
}