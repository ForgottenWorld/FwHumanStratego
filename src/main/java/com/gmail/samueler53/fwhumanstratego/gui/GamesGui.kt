package com.gmail.samueler53.fwhumanstratego.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.gmail.samueler53.fwhumanstratego.FwHumanStratego
import com.gmail.samueler53.fwhumanstratego.objects.Arena
import com.gmail.samueler53.fwhumanstratego.objects.Game
import com.gmail.samueler53.fwhumanstratego.utils.editItemMeta
import com.gmail.samueler53.fwhumanstratego.utils.itemStack
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.meta.ItemMeta

@Suppress("Unused", "MemberVisibilityCanBePrivate")
class GamesGui {

    lateinit var gui: ChestGui

    lateinit var gamesPane: OutlinePane

    private val arenaGuiItems = mutableMapOf<String, GuiItem>()


    fun onGlobalClick(event: InventoryClickEvent) {
        event.isCancelled = true
    }

    fun onOutsideClick(event: InventoryClickEvent) {
        event.isCancelled = true
    }

    fun onGameCreated(arena: Arena, game: Game) {
        val itemStack = Material.NETHERITE_BLOCK.itemStack {
            editItemMeta {
                setDisplayName(arena.name)
                setGameInfoLoreStrings(game)
            }
        }
        val guiItem = GuiItem(itemStack) {
            game.onPlayerJoin(it.whoClicked as Player)
        }
        arenaGuiItems[arena.name] = guiItem
        gamesPane.addItem(guiItem)
        gui.update()
    }

    fun onGameRemoved(game: Game) {
        val guiItem = arenaGuiItems[game.arena.name] ?: return
        arenaGuiItems.remove(game.arena.name)
        gamesPane.removeItem(guiItem)
        gui.update()
    }

    fun updateGameInfo(game: Game) {
        arenaGuiItems[game.arena.name]?.item?.editItemMeta {
            setGameInfoLoreStrings(game)
        }
        gui.update()
    }

    private fun ItemMeta.setGameInfoLoreStrings(game: Game) {
        lore = listOf("${game.playersPlaying.size}/${game.numberOfPlayers}")
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