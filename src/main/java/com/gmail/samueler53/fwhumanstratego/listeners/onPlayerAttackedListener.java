package com.gmail.samueler53.fwhumanstratego.listeners;

import com.gmail.samueler53.fwhumanstratego.managers.ArenaManager;
import com.gmail.samueler53.fwhumanstratego.managers.GameManager;
import com.gmail.samueler53.fwhumanstratego.message.Message;
import com.gmail.samueler53.fwhumanstratego.objects.Game;
import com.gmail.samueler53.fwhumanstratego.objects.Role;
import com.gmail.samueler53.fwhumanstratego.objects.Squad;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;

public class onPlayerAttackedListener implements Listener {

    final GameManager gameManager = GameManager.getInstance();
    final ArenaManager arenaManager = ArenaManager.getInstance();

    @EventHandler
    public void onPlayerAttacked(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damageDealer = (Player) event.getDamager();
            UUID uuidDamageDealer = event.getDamager().getUniqueId();
            UUID uuidDamaged = event.getEntity().getUniqueId();
            if (gameManager.areInTheSameGame(uuidDamaged, uuidDamageDealer)) {
                Game game = GameManager.getInstance().getGameWherePlayerPlaying(uuidDamaged);
                if (!game.isStarted()) {
                    Message.GAME_PREPARATIONFASE.send(damageDealer);
                } else {
                    if (game.areInTheSameTeam(uuidDamageDealer, uuidDamaged)) {
                        Message.GAME_SAMETEAM.send(damageDealer);
                    } else if (game.getRoleFromPlayer(uuidDamageDealer) != null && game.getRoleFromPlayer(uuidDamaged) != null) {
                        Role roleDamageDealer = game.getRoleFromPlayer(uuidDamageDealer);
                        Role roleDamaged = game.getRoleFromPlayer(uuidDamaged);
                        if (!roleDamaged.getName().equals(roleDamageDealer.getName()) && !game.isBomb(uuidDamageDealer) && !(game.isGeneral(uuidDamageDealer) && game.isBomb(uuidDamaged))) {
                            winnerThing(game.getWhoWin(uuidDamageDealer, uuidDamaged), game);
                            looserThing(game.getWhoLose(uuidDamageDealer, uuidDamaged), game);
                        } else if (game.isBomb(uuidDamageDealer)) {
                            Message.GAME_BOMB.send(damageDealer);
                        }
                    }
                }
                event.setCancelled(true);
            }
        }
    }

    private void winnerThing(UUID uuidWinner, Game game) {
        Player winner = Bukkit.getPlayer(uuidWinner);
        if (winner == null) {
            return;
        }
        winner.playSound(winner.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 10.0F, 10.0F);
        if (game.isBomb(uuidWinner)) {
            winner.getWorld().createExplosion(winner.getLocation(), 0.0F);
        }
    }

    private void looserThing(UUID uuidLooser, Game game) {
        Player looser = Bukkit.getPlayer(uuidLooser);
        if (looser == null) {
            return;
        }
        Squad hisSquad = game.getSquadFromPlayer(uuidLooser);
        Squad otherSquad = game.getOtherSquad(hisSquad);
        otherSquad.addPoints(game.getRoleFromPlayer(uuidLooser).getPoints());
        game.getScoreboard().removeScoreboardForPlayer(uuidLooser);
        arenaManager.teleportPlayerToHisSpawnPoint(uuidLooser, game);
        if (game.isGeneral(uuidLooser)) {
            Message.GAME_GENERALDEAD.broadcast(game);
            game.endRound(otherSquad);
        } else if (game.hasStoleWool(uuidLooser)) {
            game.stolenWool(uuidLooser);
            Message.GAME_TREASURESAVED.broadcast(game, looser.getDisplayName());
        }
        hisSquad.getPlayersRoles().put(uuidLooser, null);
        hisSquad.getRoleGui().updateGui();
    }
}

