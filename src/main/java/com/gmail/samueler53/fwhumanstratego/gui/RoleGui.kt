package com.gmail.samueler53.fwhumanstratego.gui

import com.github.stefvanschie.inventoryframework.Gui
import com.github.stefvanschie.inventoryframework.GuiItem
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.gmail.samueler53.fwhumanstratego.message.Message
import com.gmail.samueler53.fwhumanstratego.objects.Game
import com.gmail.samueler53.fwhumanstratego.objects.Role
import com.gmail.samueler53.fwhumanstratego.objects.Team
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*

class RoleGui(private val team: Team, val game: Game) {

    private var mainGui: Gui = prepareGui()

    private fun prepareGui(): Gui {
        mainGui = Gui(3, "Role").apply {
            mainGui.setOnGlobalClick { event: InventoryClickEvent -> event.isCancelled = true }
            mainGui.addPane(OutlinePane(0, 0, 9, 3).apply {
                addItem(GuiItem(ItemStack(Material.BLACK_STAINED_GLASS_PANE)))
                setRepeat(true)
            })
        }
        return mainGui
    }

    fun addItemStack() {
        val rolePane = OutlinePane(1, 1, 7, 1)
        val generaleStack = generaleItemStack()
        val marescialloStack = marescialloItemStack()
        val colonnelloStack = colonnelloItemStack()
        val maggioreStack = maggioreItemStack()
        val artificiereStack = artificiereItemStack()
        val bombaStack = bombaItemStack()
        val assassinoStack = assassinoItemStack()
        mainGui.setOnOutsideClick { event: InventoryClickEvent -> event.isCancelled = true }
        rolePane.addItem(GuiItem(generaleStack) {
            replaceRole(it.whoClicked as? Player ?: return@GuiItem, "generale")
        })
        rolePane.addItem(GuiItem(marescialloStack) {
            replaceRole(it.whoClicked as? Player ?: return@GuiItem, "maresciallo")
        })
        rolePane.addItem(GuiItem(colonnelloStack) {
            replaceRole(it.whoClicked as? Player ?: return@GuiItem, "colonnello")
        })
        rolePane.addItem(GuiItem(maggioreStack) {
            replaceRole(it.whoClicked as? Player ?: return@GuiItem, "maggiore")
        })
        rolePane.addItem(GuiItem(artificiereStack) {
            replaceRole(it.whoClicked as? Player ?: return@GuiItem, "artificiere")
        })
        rolePane.addItem(GuiItem(bombaStack) {
            replaceRole(it.whoClicked as? Player ?: return@GuiItem, "bomba")
        })
        rolePane.addItem(GuiItem(assassinoStack) {
            replaceRole(it.whoClicked as? Player ?: return@GuiItem, "assassino")
        })
        mainGui.addPane(rolePane)
    }

    private fun getRoleItemStack(role: Role, material: Material): ItemStack {
        val remaining = team.rolesRemaining[role]
        val left = role.maxPlayers - game.countPlayersWithRole(role, team)
        return ItemStack(material).apply {
            itemMeta = itemMeta?.apply {
                lore = listOf(
                    "Puoi utilizzare questo ruolo ancora $remaining volte",
                    "Ce ne possono essere altri $left in gioco"
                )
                setDisplayName(role.displayName)
            }
        }
    }

    private fun generaleItemStack(): ItemStack {
        val generale by game.roles()
        return getRoleItemStack(generale, Material.NETHERITE_SWORD)
    }

    private fun marescialloItemStack(): ItemStack {
        val maresciallo by game.roles()
        return getRoleItemStack(maresciallo, Material.DIAMOND_SWORD)
    }

    private fun colonnelloItemStack(): ItemStack {
        val colonnello by game.roles()
        return getRoleItemStack(colonnello, Material.IRON_SWORD)
    }

    private fun maggioreItemStack(): ItemStack {
        val maggiore by game.roles()
        return getRoleItemStack(maggiore, Material.STONE_SWORD)
    }

    private fun artificiereItemStack(): ItemStack {
        val artificiere by game.roles()
        return getRoleItemStack(artificiere, Material.FLINT_AND_STEEL)
    }

    private fun bombaItemStack(): ItemStack {
        val bomba by game.roles()
        return getRoleItemStack(bomba, Material.TNT)
    }

    private fun assassinoItemStack(): ItemStack {
        val assassino by game.roles()
        return getRoleItemStack(assassino, Material.LEAD)
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

    private fun replaceRole(player: Player, roleName: String) {
        val role = game.getRoleByName(roleName) ?: return

        if (!game.isRoleAvailableForTeam(role, team)) {
            Message.GAME_ROLEBUSY.send(player)
            return
        }

        if (!game.isRemainingARole(role, team)) {
            Message.GAME_ROLEFULL.send(player)
            return
        }

        game.addPlayerRole(player, role, team)
        player.sendTitle("Il tuo nuovo ruolo e'", role.name, 30, 100, 30)
        team.rolesRemaining[role] = team.rolesRemaining[role]!! - 1
        updateGui()
        game.scoreboard.updatePlayerRole(player)
        clearPlayer(player)
        player.closeInventory()
        game.messageRole(player)

        if (game.isSpectateMode(team)) {
            game.spectateMode(team)
        }
    }

    private fun clearPlayer(player: Player) {
        player.foodLevel = 20
        for (effect in player.activePotionEffects) {
            player.removePotionEffect(effect.type)
        }
        if (player.gameMode != GameMode.SURVIVAL) {
            player.gameMode = GameMode.SURVIVAL
        }
        if (player.walkSpeed != 0.2f) {
            player.walkSpeed = 0.2f
        }
    }
}