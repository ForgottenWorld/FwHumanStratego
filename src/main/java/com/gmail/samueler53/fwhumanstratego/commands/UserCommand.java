package com.gmail.samueler53.fwhumanstratego.commands;

import com.gmail.samueler53.fwhumanstratego.managers.GameManager;
import com.gmail.samueler53.fwhumanstratego.message.Message;
import com.gmail.samueler53.fwhumanstratego.objects.Game;
import com.gmail.samueler53.fwhumanstratego.objects.Role;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UserCommand implements CommandExecutor {

    GameManager gameManager = GameManager.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();
            if (player.hasPermission("humanstratego.usercommand")) {
                if (args.length == 1) {
                    switch (args[0]) {
                        case "join": {
                            if (!gameManager.isPlayerPlaying(uuid).isPresent()) {
                                playerJoin(uuid);
                            } else {
                                Message.GAME_LEAVEGAMEFIRST.send(player);
                            }
                            break;
                        }
                        case "leave": {
                            if (gameManager.isPlayerPlaying(uuid).isPresent()) {
                                playerLeave(uuid);
                            }
                            break;
                        }
                        case "role": {
                            if (gameManager.isPlayerPlaying(uuid).isPresent()) {
                                playerRole(uuid);
                            }
                            break;
                        }
                    }
                } else if (args.length == 2) {
                    switch (args[0]) {
                        case "join": {
                            if (gameManager.isPlayerPlaying(uuid).isPresent() && args[1].equalsIgnoreCase("team")) {
                                playerJoinTeam(uuid);
                            }
                            break;
                        }
                        case "info": {
                            if (gameManager.isPlayerPlaying(uuid).isPresent()) {
                                infoRole(uuid, args[1]);
                            }
                            break;
                        }
                    }
                }
            }
        }
        return true;
    }

    private void playerJoin(UUID uuid) {
        gameManager.getGamesGui().show(uuid);
    }

    private void playerLeave(UUID uuid) {
        Game game = gameManager.getGameWherePlayerPlaying(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }
        if (!game.isStarted()) {
            Message.GAME_LEAVE.send(player);
            if (game.hasASquad(uuid)) {
                game.getSquadFromPlayer(uuid).removePlayer(uuid);
                game.getTeamGui().updateGui();
            }
            game.removeAPlayer(uuid);
            gameManager.getGamesGui().modifyGame(game);
            player.teleport(game.getPlayersLocations().get(uuid));
            game.getPlayersLocations().remove(uuid);
        } else {
            Message.GAME_LEAVEWHENSTARTED.send(player);
        }
    }

    private void playerRole(UUID uuid) {
        Game game = gameManager.getGameWherePlayerPlaying(uuid);
        if (game.isStarted() && game.getRoleFromPlayer(uuid) == null) {
            game.getSquadFromPlayer(uuid).getRoleGui().show(uuid);
        }
    }

    private void playerJoinTeam(UUID uuid) {
        Game game = gameManager.getGameWherePlayerPlaying(uuid);
        if (!game.isStarted()) {
            game.getTeamGui().show(uuid);
        }
    }

    private void infoRole(UUID uuid, String roleName) {
        Game game = gameManager.getGameWherePlayerPlaying(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }
        if (game.isStarted()) {
            if (game.getRoleByName(roleName) != null) {
                Role role = game.getRoleByName(roleName);
                player.sendMessage(role.getDescription());
            }
        }
    }
}
