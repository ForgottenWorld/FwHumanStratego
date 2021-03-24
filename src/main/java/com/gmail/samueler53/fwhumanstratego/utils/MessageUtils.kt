package com.gmail.samueler53.fwhumanstratego.utils

import org.bukkit.ChatColor

object MessageUtils {

    fun formatSuccessMessage(message: String) = "${ChatColor.GREEN}$message"

    fun formatErrorMessage(message: String) = "${ChatColor.RED}$message"

    fun rewritePlaceholders(input: String): String {
        var output = input
        var i = 0
        while (output.contains("{}")) {
            output = output.replaceFirst("{}", "{" + i++ + "}")
        }
        return output
    }
}