package com.gmail.samueler53.fwhumanstratego.gui

import com.github.stefvanschie.inventoryframework.Gui
import com.github.stefvanschie.inventoryframework.GuiItem
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.gmail.samueler53.fwhumanstratego.message.Message
import com.gmail.samueler53.fwhumanstratego.objects.Game
import com.gmail.samueler53.fwhumanstratego.objects.Team
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class TeamGui(private val game: Game) {

    private var mainGui = prepareGui()

    private fun prepareGui(): Gui {
        mainGui = Gui(3, "TeamsGui")
        mainGui.setOnGlobalClick { event: InventoryClickEvent -> event.isCancelled = true }
        val background = OutlinePane(0, 0, 9, 3)
        background.addItem(GuiItem(ItemStack(Material.BLACK_STAINED_GLASS_PANE)))
        background.setRepeat(true)
        mainGui.addPane(background)
        addItemStack()
        return mainGui
    }

    private fun addItemStack() {
        val redTeamPane = OutlinePane(3, 1, 1, 1)
        val blueTeamPane = OutlinePane(5, 1, 6, 1)
        val redTeamItemStack = redTeamItemStack()
        val blueTeamItemStack = blueTeamItemStack()
        mainGui.setOnOutsideClick { event: InventoryClickEvent -> event.isCancelled = true }
        redTeamPane.addItem(GuiItem(redTeamItemStack) {
            chooseTeam(it.whoClicked as? Player ?: return@GuiItem, game.redTeam)
        })
        blueTeamPane.addItem(GuiItem(blueTeamItemStack) {
            chooseTeam(it.whoClicked as? Player ?: return@GuiItem, game.blueTeam)
        })
        mainGui.addPane(redTeamPane)
        mainGui.addPane(blueTeamPane)
    }

    private fun redTeamItemStack() = ItemStack(Material.RED_WOOL).apply {
        itemMeta = itemMeta?.apply {
            setDisplayName("Team Rosso ${game.redTeam.playersRoles.size}/${game.numberOfPlayers / 2}")
        }
    }

    private fun blueTeamItemStack() = ItemStack(Material.BLUE_WOOL).apply {
        itemMeta = itemMeta?.apply {
            setDisplayName("Team Blu ${game.blueTeam.playersRoles.size}/${game.numberOfPlayers / 2}")
        }
    }

    fun updateGui() {
        addItemStack()
        mainGui.update()
    }

    fun show(player: Player) {
        mainGui.show(player)
    }

    val inventory: Inventory
        get() = mainGui.inventory

    private fun chooseTeam(player: Player, team: Team) {
        if (team.playersRoles.size == game.numberOfPlayers / 2) {
            Message.GAME_TEAMFULL.send(player)
            return
        }

        if (team.playersRoles.containsKey(player.uniqueId)) {
            team.removePlayer(player)
        } else {
            val otherTeam = game.getOtherTeam(team)
            if (otherTeam.playersRoles.containsKey(player.uniqueId)) {
                otherTeam.removePlayer(player)
            }
        }

        game.teamAssignment(player, team)
        updateGui()
        player.closeInventory()
        when (team.type) {
            Team.Type.RED -> Message.GAME_TEAMRED.send(player)
            Team.Type.BLUE -> Message.GAME_TEAMBLUE.send(player)
        }
        if (game.isReadyToStart()) {
            Message.GAME_ISSTARTING.broadcast(game)
            game.start()
        }
    }
}