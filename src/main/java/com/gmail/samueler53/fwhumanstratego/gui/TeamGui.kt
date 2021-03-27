package com.gmail.samueler53.fwhumanstratego.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.gmail.samueler53.fwhumanstratego.FwHumanStratego
import com.gmail.samueler53.fwhumanstratego.objects.Game
import com.gmail.samueler53.fwhumanstratego.utils.editItemMeta
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

@Suppress("Unused", "MemberVisibilityCanBePrivate")
class TeamGui private constructor(
    private val game: Game,
) : ChestGuiController() {

    override lateinit var gui: ChestGui

    lateinit var redTeamGuiItem: GuiItem

    lateinit var blueTeamGuiItem: GuiItem


    fun onGlobalClick(event: InventoryClickEvent) {
        event.isCancelled = true
    }

    fun onOutsideClick(event: InventoryClickEvent) {
        event.isCancelled = true
    }

    fun onRedTeamClicked(event: InventoryClickEvent) {
        chooseTeam(event.whoClicked as Player, game.redTeam)
    }

    fun onBlueTeamClicked(event: InventoryClickEvent) {
        chooseTeam(event.whoClicked as Player, game.blueTeam)
    }

    fun update() {
        redTeamGuiItem.item.editItemMeta {
            setDisplayName("Team Rosso ${game.redTeam.playersRoles.size}/${game.numberOfPlayers / 2}")
        }
        blueTeamGuiItem.item.editItemMeta {
            setDisplayName("Team Blu ${game.blueTeam.playersRoles.size}/${game.numberOfPlayers / 2}")
        }
        gui.update()
    }

    private fun chooseTeam(player: Player, team: Game.Team) {
        game.onPlayerChooseTeam(player, team)
    }

    companion object {

        fun newInstance(game: Game): TeamGui {
            val res = FwHumanStratego.instance.getResource("team_gui.xml")!!
            return TeamGui(game).apply {
                gui = res.use { ChestGui.load(this, it)!! }
                gui.inventory.holder
                update()
            }
        }
    }
}