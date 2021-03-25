package com.gmail.samueler53.fwhumanstratego.gui

import com.github.stefvanschie.inventoryframework.Gui
import com.github.stefvanschie.inventoryframework.GuiItem
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.gmail.samueler53.fwhumanstratego.message.Message
import com.gmail.samueler53.fwhumanstratego.objects.Game
import com.gmail.samueler53.fwhumanstratego.objects.Team
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class TeamGui(private val game: Game) {

    private var mainGui = Gui(3, "TeamsGui").apply {
        setOnGlobalClick { it.isCancelled = true }
        addPane(OutlinePane(0, 0, 9, 3).apply {
            addItem(GuiItem(ItemStack(Material.BLACK_STAINED_GLASS_PANE)))
            setRepeat(true)
        })
    }

    init {
        addItemStack()
    }

    private fun addItemStack() {
        with (mainGui) {
            setOnOutsideClick { it.isCancelled = true }
            addPane(OutlinePane(3, 1, 1, 1).apply {
                addItem(GuiItem(redTeamItemStack()) {
                    chooseTeam(it.whoClicked as? Player ?: return@GuiItem, game.redTeam)
                })
            })
            addPane(OutlinePane(5, 1, 6, 1).apply {
                addItem(GuiItem(blueTeamItemStack()) {
                    chooseTeam(it.whoClicked as? Player ?: return@GuiItem, game.blueTeam)
                })
            })
        }
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