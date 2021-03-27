package com.gmail.samueler53.fwhumanstratego.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.gmail.samueler53.fwhumanstratego.FwHumanStratego
import com.gmail.samueler53.fwhumanstratego.managers.GameManager
import com.gmail.samueler53.fwhumanstratego.utils.editItemMeta
import com.gmail.samueler53.fwhumanstratego.utils.itemStack
import com.gmail.samueler53.fwhumanstratego.utils.setLore
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

@Suppress("Unused", "MemberVisibilityCanBePrivate")
class GamesGui private constructor() : ChestGuiController() {

    override lateinit var gui: ChestGui

    lateinit var gamesPane: OutlinePane

    private val arenaGuiItems = mutableMapOf<String, GuiItem>()


    fun onGlobalClick(event: InventoryClickEvent) {
        event.isCancelled = true
    }

    fun onOutsideClick(event: InventoryClickEvent) {
        event.isCancelled = true
    }

    fun update() {
        gamesPane.clear()
        for (game in GameManager.getAllGames()) {
            val itemStack = Material.NETHERITE_BLOCK.itemStack {
                editItemMeta {
                    setDisplayName(game.arena.name)
                    setLore("${game.players.size}/${game.numberOfPlayers}")
                }
            }
            gamesPane.addItem(GuiItem(itemStack) {
                game.onPlayerJoin(it.whoClicked as Player)
            })
        }
        gui.update()
    }

    companion object {

        fun newInstance(): GamesGui {
            val res = FwHumanStratego.instance.getResource("games_gui.xml")!!
            return GamesGui().apply {
                gui = res.use { ChestGui.load(this, it)!! }
            }
        }
    }
}