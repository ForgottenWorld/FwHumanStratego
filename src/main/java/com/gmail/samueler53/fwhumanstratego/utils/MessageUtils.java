package com.gmail.samueler53.fwhumanstratego.utils;

import org.bukkit.ChatColor;

public class MessageUtils {

    public static String EOL = "\n";

    public static String getPluginPrefix() {
        return ChatColor.DARK_GRAY + "[" +
                ChatColor.YELLOW + "Fw" +
                ChatColor.GOLD + ChatColor.BOLD + "HumanStratego" +
                ChatColor.DARK_GRAY + "]";
    }

    public static String chatHeader() {
        return  ChatColor.YELLOW + "oOo--------------------[ " +
                ChatColor.YELLOW + "Fw" +
                ChatColor.GOLD + ChatColor.BOLD + "HumanStratego" +
                ChatColor.YELLOW + " ]-------------------oOo ";
    }

    public static String formatSuccessMessage(String message) {
        message = ChatColor.GREEN + message;
        return message;
    }

    public static String formatErrorMessage(String message) {
        message = ChatColor.RED + message;
        return message;
    }

    public static String helpMessage() {
        String message = chatHeader();
        return message;
    }

    public static String rewritePlaceholders(String input) {
        int i = 0;
        while (input.contains("{}")) {
            input = input.replaceFirst("\\{\\}", "{" + i++ + "}");
        }
        return input;
    }

}
