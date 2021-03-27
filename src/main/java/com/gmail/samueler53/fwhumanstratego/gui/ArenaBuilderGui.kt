package com.gmail.samueler53.fwhumanstratego.gui

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.gmail.samueler53.fwhumanstratego.FwHumanStratego
import com.gmail.samueler53.fwhumanstratego.managers.ArenaManager
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

@Suppress("Unused", "MemberVisibilityCanBePrivate")
class ArenaBuilderGui private constructor() : ChestGuiController() {

    override lateinit var gui: ChestGui


    fun onGlobalClick(event: InventoryClickEvent) {
        event.isCancelled = true
    }

    fun onRedSpawnClicked(event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        ArenaManager.setRedTeamLocation(player)
        player.closeInventory()
    }

    fun onBlueSpawnClicked(event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        ArenaManager.setBlueTeamLocation(player)
        player.closeInventory()
    }

    fun onRedTreasureClicked(event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        ArenaManager.setTreasureRedLocation(player)
        player.closeInventory()
    }

    fun onBlueTreasureClicked(event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        ArenaManager.setTreasureBlueLocation(player)
        player.closeInventory()
    }

    fun onLobbyClicked(event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        ArenaManager.setLobbyLocation(player)
        player.closeInventory()
    }

    companion object {

        fun newInstance(): ArenaBuilderGui {
            val res = FwHumanStratego.instance.getResource("arena_builder_gui.xml")!!
            return ArenaBuilderGui().apply {
                gui = res.use { ChestGui.load(this, it)!! }
            }
        }
    }
}