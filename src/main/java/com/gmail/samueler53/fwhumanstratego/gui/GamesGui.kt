package com.gmail.samueler53.fwhumanstratego.gui

import com.github.stefvanschie.inventoryframework.Gui
import com.github.stefvanschie.inventoryframework.GuiItem
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.gmail.samueler53.fwhumanstratego.managers.ArenaManager
import com.gmail.samueler53.fwhumanstratego.message.Message
import com.gmail.samueler53.fwhumanstratego.objects.Arena
import com.gmail.samueler53.fwhumanstratego.objects.Game
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.*

class GamesGui {

    private val mainGui = Gui(1, "Games").apply {
        setOnGlobalClick { it.isCancelled = true }
        addPane(OutlinePane(0, 0, 9, 1).apply {
            addItem(GuiItem(ItemStack(Material.BLACK_STAINED_GLASS_PANE)))
            setRepeat(true)
        })
        addPane(OutlinePane(0, 0, 9, 1))
    }

    fun createNewGame(arena: Arena, game: Game) {
        val itemStack = ItemStack(Material.NETHERITE_BLOCK)
        itemStack.itemMeta = itemStack.itemMeta?.apply {
            setDisplayName(arena.name)
            lore = listOf("${game.playersPlaying.size}/${game.numberOfPlayers}")
        }
        addGame(itemStack, game)
    }

    private fun addGame(itemStack: ItemStack, game: Game) {
        val gamesPane = mainGui.panes[1] as OutlinePane
        mainGui.setOnOutsideClick { event: InventoryClickEvent -> event.isCancelled = true }
        gamesPane.addItem(GuiItem(itemStack) { event: InventoryClickEvent ->
            addPlayer(event.whoClicked as? Player ?: return@GuiItem, game)
        })
        mainGui.addPane(gamesPane)
        mainGui.update()
    }

    fun removeGame(game: Game) {
        mainGui.panes[1].items.removeIf {
            it.item.itemMeta?.displayName.equals(game.arena.name, ignoreCase = true)
        }
        mainGui.update()
    }

    fun modifyGame(game: Game) {
        val items = mainGui.panes[1].items
        for (guiItem in items) {
            if (!guiItem
                    .item
                    .itemMeta!!
                    .displayName
                    .equals(game.arena.name, ignoreCase = true)
            ) continue
            guiItem.item.itemMeta = guiItem.item.itemMeta?.apply {
                lore = listOf("${game.playersPlaying.size}/${game.numberOfPlayers}")
            }
            mainGui.update()
        }
    }

    fun getNumberOfGamesInTheGui() = mainGui.panes[1].items.size

    fun show(player: Player) {
        mainGui.show(player)
    }

    private fun addPlayer(player: Player, game: Game) {
        if (game.numberOfPlayers == game.playersPlaying.size) {
            Message.GAME_GAMEFULL.send(player)
            return
        }
        Message.GAME_JOIN.send(player)
        game.addPlayer(player)
        game.playersLocations[player.uniqueId] = player.location
        ArenaManager.teleportPlayerToLobby(player, game.arena)
        modifyGame(game)
        game.clearPlayer(player)
        player.closeInventory()
    }

}