package com.gmail.samueler53.fwhumanstratego.listeners;

import com.gmail.samueler53.fwhumanstratego.managers.GameManager;
import com.gmail.samueler53.fwhumanstratego.message.Message;
import com.gmail.samueler53.fwhumanstratego.objects.Game;
import com.gmail.samueler53.fwhumanstratego.objects.Role;
import com.gmail.samueler53.fwhumanstratego.objects.Squad;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class onPlayerLeftListener implements Listener {

    final GameManager gameManager = GameManager.getInstance();

    @EventHandler
    public void onPlayerLeft(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (gameManager.isPlayerPlaying(uuid).isPresent()) {
            playerLeft(uuid);
        }
    }

    private void playerLeft(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        Game game = gameManager.getGameWherePlayerPlaying(uuid);
        if (game.hasASquad(uuid)) {
            Squad squad = game.getSquadFromPlayer(uuid);
            if (game.isStarted()) {
                Message.GAME_DESERTER.broadcast(game, player.getDisplayName());
                if (game.hasARole(uuid)) {
                    Role role = game.getRoleFromPlayer(uuid);
                    squad.getRolesRemaining().put(role, squad.getRolesRemaining().get(role) + 1);
                    squad.getPlayersRoles().remove(uuid);
                    squad.getRoleGui().updateGui();
                    if (game.hasStoleWool(uuid)) {
                        game.stolenWool(uuid);
                        Message.GAME_TREASURESAVED.broadcast(game, player.getDisplayName());
                    }
                }
            }
            squad.removePlayer(uuid);
            game.getTeamGui().updateGui();
        }
        game.clearPlayer(uuid);
        game.removeAPlayer(uuid);
        player.teleport(game.getPlayersLocations().get(uuid));
        game.getPlayersLocations().remove(uuid);
    }
}