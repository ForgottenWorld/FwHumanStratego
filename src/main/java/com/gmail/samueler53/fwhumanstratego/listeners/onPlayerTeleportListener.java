package com.gmail.samueler53.fwhumanstratego.listeners;

import com.gmail.samueler53.fwhumanstratego.FwHumanStratego;
import com.gmail.samueler53.fwhumanstratego.gui.TeamGui;
import com.gmail.samueler53.fwhumanstratego.managers.GameManager;
import com.gmail.samueler53.fwhumanstratego.message.Message;
import com.gmail.samueler53.fwhumanstratego.objects.Game;
import com.gmail.samueler53.fwhumanstratego.objects.Squad;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class onPlayerTeleportListener implements Listener {

    GameManager gameManager = GameManager.getInstance();

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (gameManager.isPlayerPlaying(uuid).isPresent()) {
            Game game = gameManager.getGameWherePlayerPlaying(uuid);
            if (game.getArena().getLobbyLocation().equals(event.getTo())) {
                lobbyTeleport(uuid, game);
            } else if (game.getArena().getRedTeamLocation().equals(event.getTo()) || game.getArena().getBlueTeamLocation().equals(event.getTo())) {
                spawnTeleport(uuid, game);
            }
        }
    }

    private void lobbyTeleport(UUID uuid, Game game) {
        Player player = Bukkit.getPlayer(uuid);
        assert player != null;
        Message.GAME_CHOOSETEAM.send(player);
        TeamGui teamGui = game.getTeamGui();
        Bukkit.getScheduler().runTaskLater(FwHumanStratego.getPlugin(), () -> {
            teamGui.show(uuid);
        }, 1L);
    }

    private void spawnTeleport(UUID uuid, Game game) {
        Player player = Bukkit.getPlayer(uuid);
        assert player != null;
        Message.GAME_CHOOSEROLE.send(player);
        Squad squad = game.getSquadFromPlayer(uuid);
        Bukkit.getScheduler().runTaskLater(FwHumanStratego.getPlugin(), () -> {
            squad.getRoleGui().show(uuid);
        }, 1L);
        if (game.isSpectateMode(squad)) {
            player.setGameMode(GameMode.SPECTATOR);
        }
        selectionRoleEffects(uuid);
    }

    private void selectionRoleEffects(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        assert player != null;
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 2147483647, 250));
        player.setWalkSpeed(0.0F);
        player.setFoodLevel(4);
    }
}
