package com.gmail.samueler53.fwhumanstratego.gui

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import org.bukkit.entity.Player

abstract class ChestGuiController {

    protected abstract val gui: ChestGui

    fun show(player: Player) {
        gui.show(player)
    }
}