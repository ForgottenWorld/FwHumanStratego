package com.gmail.samueler53.fwhumanstratego.gui

import com.github.stefvanschie.inventoryframework.Gui
import com.github.stefvanschie.inventoryframework.GuiItem
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.gmail.samueler53.fwhumanstratego.FwHumanStratego
import com.gmail.samueler53.fwhumanstratego.message.Message
import com.gmail.samueler53.fwhumanstratego.objects.Game
import com.gmail.samueler53.fwhumanstratego.objects.Team
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
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
        rolePane.addItem(GuiItem(generaleStack) { event: InventoryClickEvent ->
            replaceRole(event.whoClicked as? Player ?: return@GuiItem, "generale")
        })
        rolePane.addItem(GuiItem(marescialloStack) { event: InventoryClickEvent ->
            replaceRole(event.whoClicked as? Player ?: return@GuiItem, "maresciallo")
        })
        rolePane.addItem(GuiItem(colonnelloStack) { event: InventoryClickEvent ->
            replaceRole(event.whoClicked as? Player ?: return@GuiItem, "colonnello")
        })
        rolePane.addItem(GuiItem(maggioreStack) { event: InventoryClickEvent ->
            replaceRole(event.whoClicked as? Player ?: return@GuiItem, "maggiore")
        })
        rolePane.addItem(GuiItem(artificiereStack) { event: InventoryClickEvent ->
            replaceRole(event.whoClicked as? Player ?: return@GuiItem, "artificiere")
        })
        rolePane.addItem(GuiItem(bombaStack) { event: InventoryClickEvent ->
            replaceRole(event.whoClicked as? Player ?: return@GuiItem, "bomba")
        })
        rolePane.addItem(GuiItem(assassinoStack) { event: InventoryClickEvent ->
            replaceRole(event.whoClicked as? Player ?: return@GuiItem, "assassino")
        })
        mainGui.addPane(rolePane)
    }

    private fun generaleItemStack(): ItemStack {
        val generaleStack = ItemStack(Material.NETHERITE_SWORD)
        val generaleMeta = generaleStack.itemMeta ?: return generaleStack
        val config: FileConfiguration = FwHumanStratego.defaultConfig
        val loreStrings = mutableListOf<String>()
        loreStrings.add(
            "Puoi utilizzare questo ruolo ancora ${
                team.rolesRemaining[game.getRoleByName("Generale")]
            } volte"
        )
        loreStrings.add(
            "Ci possono essere altri ${
                config.getInt("roles.Generale.max_players") - game.getPlayerWhoHaveThisRole(
                    game.getRoleByName("Generale")!!,
                    team
                )
            } generali in gioco"
        )
        generaleMeta.lore = loreStrings
        generaleMeta.setDisplayName("Generale")
        generaleStack.itemMeta = generaleMeta
        return generaleStack
    }

    private fun marescialloItemStack(): ItemStack {
        val marescialloStack = ItemStack(Material.DIAMOND_SWORD)
        val marescialloMeta = marescialloStack.itemMeta ?: return marescialloStack
        val config = FwHumanStratego.defaultConfig
        val loreStrings = listOf(
            "Puoi utilizzare questo ruolo ancora ${
                team.rolesRemaining[game.getRoleByName("Maresciallo")]
            } volte",
            "Ci possono essere altri ${
                config.getInt("roles.Maresciallo.max_players") - game.getPlayerWhoHaveThisRole(
                    game.getRoleByName("Maresciallo")!!,
                    team
                )
            } marescialli in gioco"
        )
        marescialloMeta.lore = loreStrings
        marescialloMeta.setDisplayName("Maresciallo")
        marescialloStack.itemMeta = marescialloMeta
        return marescialloStack
    }

    private fun colonnelloItemStack(): ItemStack {
        val colonnelloStack = ItemStack(Material.IRON_SWORD)
        val colonnelloMeta = colonnelloStack.itemMeta ?: return colonnelloStack
        val config = FwHumanStratego.defaultConfig
        val loreStrings = listOf(
            "Puoi utilizzare questo ruolo ancora ${
                team.rolesRemaining[game.getRoleByName("Colonnello")]
            } volte",
            "Ci possono essere altri ${
                config.getInt("roles.Colonnello.max_players") - game.getPlayerWhoHaveThisRole(
                    game.getRoleByName("Colonnello")!!,
                    team
                )
            } colonnelli in gioco"
        )
        colonnelloMeta.lore = loreStrings
        colonnelloMeta.setDisplayName("Colonnello")
        colonnelloStack.itemMeta = colonnelloMeta
        return colonnelloStack
    }

    private fun maggioreItemStack(): ItemStack {
        val maggioreStack = ItemStack(Material.STONE_SWORD)
        val maggioreMeta = maggioreStack.itemMeta ?: return maggioreStack
        val loreStrings = mutableListOf<String>()
        loreStrings.add(
            "Puoi utilizzare questo ruolo ancora ${
                team.rolesRemaining[game.getRoleByName("Maggiore")]
            } volte"
        )
        loreStrings.add(
            "Ci possono essere altri ${
                FwHumanStratego.defaultConfig.getInt("roles.Maggiore.max_players") - game.getPlayerWhoHaveThisRole(
                    game.getRoleByName(
                        "Maggiore"
                    )!!, team
                )
            } maggiori in gioco"
        )
        maggioreMeta.lore = loreStrings
        maggioreMeta.setDisplayName("Maggiore")
        maggioreStack.itemMeta = maggioreMeta
        return maggioreStack
    }

    private fun artificiereItemStack(): ItemStack {
        val artificiereStack = ItemStack(Material.FLINT_AND_STEEL)
        val artificiereMeta = artificiereStack.itemMeta ?: return artificiereStack
        val config: FileConfiguration = FwHumanStratego.defaultConfig
        val loreStrings = mutableListOf<String>()
        loreStrings.add(
            "Puoi utilizzare questo ruolo ancora ${
                team.rolesRemaining[game.getRoleByName("Artificiere")]
            } volte"
        )
        loreStrings.add(
            "Ci possono essere altri ${
                config.getInt("roles.Artificiere.max_players") - game.getPlayerWhoHaveThisRole(
                    game.getRoleByName("Artificiere")!!,
                    team
                )
            } artificieri in gioco"
        )
        artificiereMeta.lore = loreStrings
        artificiereMeta.setDisplayName("Artificiere")
        artificiereStack.itemMeta = artificiereMeta
        return artificiereStack
    }

    private fun bombaItemStack(): ItemStack {
        val bombaStack = ItemStack(Material.TNT)
        val bombaMeta = bombaStack.itemMeta ?: return bombaStack
        val config: FileConfiguration = FwHumanStratego.defaultConfig
        val loreStrings = mutableListOf<String>()
        loreStrings.add(
            "Puoi utilizzare questo ruolo ancora ${
                team.rolesRemaining[game.getRoleByName("Bomba")]
            } volte"
        )
        loreStrings.add(
            "Ci possono essere altri ${
                config.getInt("roles.Bomba.max_players") - game.getPlayerWhoHaveThisRole(
                    game.getRoleByName("Bomba")!!,
                    team
                )
            } bombe in gioco"
        )
        bombaMeta.lore = loreStrings
        bombaMeta.setDisplayName("Bomba")
        bombaStack.itemMeta = bombaMeta
        return bombaStack
    }

    private fun assassinoItemStack(): ItemStack {
        val loreStrings = mutableListOf<String>()
        val roleName = team.rolesRemaining[game.getRoleByName("Assassino")]
        loreStrings.add(
            "Puoi utilizzare questo ruolo ancora $roleName volte"
        )
        val left = FwHumanStratego.defaultConfig.getInt("roles.Assassino.max_players") -
            game.getPlayerWhoHaveThisRole(game.getRoleByName("Assassino")!!, team)
        loreStrings.add("Ci possono essere altri $left assassini in gioco")
        val assassinoStack = ItemStack(Material.LEAD)
        assassinoStack.itemMeta = assassinoStack.itemMeta?.apply {
            lore = loreStrings
            setDisplayName("Assassino")
        }
        return assassinoStack
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