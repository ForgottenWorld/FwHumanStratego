package com.gmail.samueler53.fwhumanstratego.listeners;

import com.gmail.samueler53.fwhumanstratego.managers.GameManager;
import com.gmail.samueler53.fwhumanstratego.objects.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Objects;
import java.util.UUID;

public class onPlayerChattingListener implements Listener {

    final GameManager gameManager = GameManager.getInstance();

    @EventHandler
    public void onPlayerChatting(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (gameManager.isPlayerPlaying(uuid).isPresent()) {
            Game game = gameManager.getGameWherePlayerPlaying(uuid);
            if (game.hasASquad(uuid)) {
                event.setCancelled(true);
                if (game.getRed().getPlayersRoles().containsKey(uuid)) {
                    for (UUID uuidPlayersInRedTeam : game.getRed().getPlayersRoles().keySet()) {
                        Objects.requireNonNull(Bukkit.getPlayer(uuidPlayersInRedTeam)).sendMessage(ChatColor.DARK_RED + "Red team " + player.getDisplayName() + ": " + event.getMessage());
                    }
                } else if (game.getBlue().getPlayersRoles().containsKey(uuid)) {
                    for (UUID uuidPlayersInBlueTeam : game.getBlue().getPlayersRoles().keySet()) {
                        Objects.requireNonNull(Bukkit.getPlayer(uuidPlayersInBlueTeam)).sendMessage(ChatColor.BLUE + "Blue team " + player.getDisplayName() + ": " + event.getMessage());
                    }
                }
            }
        }
    }
}
