package com.gmail.samueler53.fwhumanstratego.utils

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

inline fun Material.itemStack(build: ItemStack.() -> Unit = {}) = ItemStack(this).apply(build)

inline fun ItemStack.editItemMeta(edit: ItemMeta.() -> Unit): ItemStack {
    itemMeta = itemMeta?.apply(edit)
    return this
}

inline fun <reified T: ItemMeta> ItemStack.editItemMetaOfType(edit: T.() -> Unit): ItemStack {
    itemMeta = (itemMeta as T).apply(edit)
    return this
}

fun ItemMeta.setLore(vararg lines: String) {
    lore = listOf(*lines)
}
