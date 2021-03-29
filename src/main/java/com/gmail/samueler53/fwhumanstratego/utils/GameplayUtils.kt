package com.gmail.samueler53.fwhumanstratego.utils

import org.bukkit.Color
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object GameplayUtils {

    fun rootPlayer(player: Player) {
        player.addPotionEffect(
            PotionEffect(
                PotionEffectType.JUMP,
                2147483647,
                250
            )
        )
        player.walkSpeed = 0.0f
        player.foodLevel = 4
    }

    fun createDyedLeatherChestplate(color: Color) = Material.LEATHER_CHESTPLATE.itemStack {
        editItemMetaOfType<LeatherArmorMeta> {
            setColor(color)
        }
    }

    fun ensureChestExistsAtLocation(location: Location) {
        val block = location.world!!.getBlockAt(location)
        if (block.state is Chest) return
        block.setType(Material.CHEST, true)
    }

    fun cleansePlayer(player: Player, clearInventory: Boolean = true) {
        player.foodLevel = 20
        for (effect in player.activePotionEffects) {
            player.removePotionEffect(effect.type)
        }
        player.gameMode = GameMode.ADVENTURE
        player.walkSpeed = 0.2f
        player.closeInventory()
        if (clearInventory) {
            player.inventory.clear()
            player.inventory.setArmorContents(null)
        }
    }
}