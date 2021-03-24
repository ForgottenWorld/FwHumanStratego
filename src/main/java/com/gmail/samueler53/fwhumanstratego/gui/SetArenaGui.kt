package com.gmail.samueler53.fwhumanstratego.gui

import com.github.stefvanschie.inventoryframework.Gui
import com.github.stefvanschie.inventoryframework.GuiItem
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.gmail.samueler53.fwhumanstratego.managers.ArenaManager
import com.gmail.samueler53.fwhumanstratego.objects.Arena
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta

object SetArenaGui {

    fun showToPlayer(player: Player, arena: Arena) {
        with (Gui(3, "Role")) {
            setOnGlobalClick { it.isCancelled = true }

            val background = OutlinePane(0, 0, 9, 3).apply {
                addItem(GuiItem(ItemStack(Material.BLACK_STAINED_GLASS_PANE)))
                setRepeat(true)
            }

            addPane(background)

            addPane(OutlinePane(2, 1, 5, 1).apply {
                addItem(GuiItem(redTeamItemStack()) {
                    ArenaManager.setRedTeamLocation(player.location, arena, player)
                })

                addItem(GuiItem(blueTeamItemStack()) {
                    ArenaManager.setBlueTeamLocation(player.location, arena, player)
                })

                addItem(GuiItem(treasureRedItemStack()) {
                    ArenaManager.setTreasureRedLocation(player.location, arena, player)
                })

                addItem(GuiItem(treasureBlueItemStack()) {
                    ArenaManager.setTreasureBlueLocation(player.location, arena, player)
                })

                addItem(GuiItem(lobbyItemStack()) {
                ArenaManager.setLobbyLocation(player.location, arena, player)
                })
            })
            show(player)
        }
    }

    private fun getMaterial(color: Color?) = ItemStack(Material.LEATHER_CHESTPLATE).apply {
        itemMeta = (itemMeta as LeatherArmorMeta).apply {
            setColor(color)
        }
    }

    private fun redTeamItemStack() = getMaterial(Color.RED).apply {
        itemMeta = itemMeta?.apply {
            setDisplayName("Spawn del team rosso")
        }
    }

    private fun blueTeamItemStack() = getMaterial(Color.BLUE).apply {
        itemMeta = itemMeta?.apply {
            setDisplayName("Spawn del team blu")
        }
    }

    private fun treasureRedItemStack() = ItemStack(Material.RED_WOOL).apply {
        itemMeta = itemMeta?.apply {
            setDisplayName("Tesoro rosso")
        }
    }

    private fun treasureBlueItemStack() = ItemStack(Material.BLUE_WOOL).apply {
        itemMeta = itemMeta?.apply {
            setDisplayName("Tesoro blu")
        }
    }

    private fun lobbyItemStack() = ItemStack(Material.GLASS_PANE).apply {
        itemMeta = itemMeta!!.apply {
            setDisplayName("Lobby")
        }
    }
}