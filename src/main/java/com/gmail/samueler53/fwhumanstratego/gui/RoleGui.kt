package com.gmail.samueler53.fwhumanstratego.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.gmail.samueler53.fwhumanstratego.FwHumanStratego
import com.gmail.samueler53.fwhumanstratego.managers.RoleManager
import com.gmail.samueler53.fwhumanstratego.objects.Game
import com.gmail.samueler53.fwhumanstratego.objects.Role
import com.gmail.samueler53.fwhumanstratego.utils.editItemMeta
import com.gmail.samueler53.fwhumanstratego.utils.itemStack
import com.gmail.samueler53.fwhumanstratego.utils.setLore
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

@Suppress("Unused", "MemberVisibilityCanBePrivate")
class RoleGui private constructor(
    val game: Game,
    private val team: Game.Team
) : ChestGuiController() {

    override lateinit var gui: ChestGui

    lateinit var rolesPane: OutlinePane


    fun onGlobalClick(event: InventoryClickEvent) {
        event.isCancelled = true
    }

    fun onOutsideClick(event: InventoryClickEvent) {
        event.isCancelled = true
    }

    fun updateRoles() {
        rolesPane.clear()
        rolesPane.apply {
            RoleManager.roles.values
                .map(::getRoleGuiItem)
                .forEach(::addItem)
        }
        gui.update()
    }

    private fun getRoleGuiItem(role: Role): GuiItem {
        val remaining = team.rolesRemaining[role]
        val left = role.maxPlayers - team.countPlayersWithRole(role)
        val itemStack = role.material.itemStack {
            editItemMeta {
                setLore(
                    "Puoi utilizzare questo ruolo ancora $remaining volte",
                    "Ce ne possono essere altri $left in gioco"
                )
                setDisplayName(role.displayName)
            }
        }
        return GuiItem(itemStack) {
            val player = it.whoClicked as Player
            if (!game.onPlayerChangeRole(player, role)) show(player)
        }
    }

    companion object {

        fun newInstance(game: Game, team: Game.Team): RoleGui {
            val res = FwHumanStratego.instance.getResource("role_gui.xml")!!
            return RoleGui(game, team).apply {
                gui = res.use { ChestGui.load(this, it)!! }
            }
        }
    }
}