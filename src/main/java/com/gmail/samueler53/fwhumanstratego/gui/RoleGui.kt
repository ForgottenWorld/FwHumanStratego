package com.gmail.samueler53.fwhumanstratego.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.gmail.samueler53.fwhumanstratego.FwHumanStratego
import com.gmail.samueler53.fwhumanstratego.managers.RoleManager
import com.gmail.samueler53.fwhumanstratego.objects.Game
import com.gmail.samueler53.fwhumanstratego.objects.Role
import com.gmail.samueler53.fwhumanstratego.utils.editItemMeta
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

@Suppress("Unused", "MemberVisibilityCanBePrivate")
class RoleGui private constructor(val game: Game, private val team: Game.Team) {

    lateinit var gui: ChestGui

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
        val itemStack = ItemStack(role.material).editItemMeta {
            lore = listOf(
                "Puoi utilizzare questo ruolo ancora $remaining volte",
                "Ce ne possono essere altri $left in gioco"
            )
            setDisplayName(role.displayName)
        }
        return GuiItem(itemStack) {
            game.onPlayerChangeRole(it.whoClicked as Player, role)
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