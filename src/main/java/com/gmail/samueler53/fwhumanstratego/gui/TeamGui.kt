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
        game.onPlayerChooseTeam(event.whoClicked as Player, true)
    }

    fun onBlueTeamClicked(event: InventoryClickEvent) {
        game.onPlayerChooseTeam(event.whoClicked as Player, false)
    }

    fun update(redTeamSize: Int, blueTeamSize: Int) {
        redTeamGuiItem.item.editItemMeta {
            setDisplayName("Team Rosso ${redTeamSize}/${game.numberOfPlayers / 2}")
        }
        blueTeamGuiItem.item.editItemMeta {
            setDisplayName("Team Blu ${blueTeamSize}/${game.numberOfPlayers / 2}")
        }
        gui.update()
    }

    companion object {

        fun newInstance(game: Game): TeamGui {
            val res = FwHumanStratego.instance.getResource("team_gui.xml")!!
            return TeamGui(game).apply {
                gui = res.use { ChestGui.load(this, it)!! }
                gui.inventory.holder
                update(0, 0)
            }
        }
    }
}