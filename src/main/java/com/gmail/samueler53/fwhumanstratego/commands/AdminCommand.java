package com.gmail.samueler53.fwhumanstratego.commands;

import com.gmail.samueler53.fwhumanstratego.FwHumanStratego;
import com.gmail.samueler53.fwhumanstratego.managers.ArenaManager;
import com.gmail.samueler53.fwhumanstratego.managers.GameManager;
import com.gmail.samueler53.fwhumanstratego.message.Message;
import com.gmail.samueler53.fwhumanstratego.objects.Arena;
import com.gmail.samueler53.fwhumanstratego.objects.Game;
import com.gmail.samueler53.fwhumanstratego.utils.NameUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AdminCommand implements CommandExecutor, TabExecutor {
    private final FwHumanStratego plugin;
    private final List<String> commandRegistry;
    GameManager gameManager = GameManager.getInstance();
    ArenaManager arenaManager = ArenaManager.getInstance();

    public AdminCommand(FwHumanStratego plugin) {
        this.commandRegistry = new ArrayList<>();
        this.plugin = plugin;
        setup();
    }

    public void setup() {
        this.commandRegistry.add("stop");
        this.commandRegistry.add("create");
        this.commandRegistry.add("remove");
        this.commandRegistry.add("set");
        this.commandRegistry.add("start");
        this.commandRegistry.add("modify");
        this.commandRegistry.add("info");
        this.commandRegistry.add("arenas");
        this.commandRegistry.add("reload");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();
            if (player.hasPermission("humanstratego.admincommand")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("arenas")) {
                        arenaManager.getArenas(player.getUniqueId());
                    }
                    if (args[0].equalsIgnoreCase("reload")) {
                        plugin.reloadDefaultConfig();
                        Message.GAME_RELOAD.send(player);
                    }
                }
                if (args.length >= 2) {
                    if (args[0].equalsIgnoreCase("create")) {
                        arenaManager.createArena(args[1], player.getUniqueId());
                    }
                    if (arenaManager.getSearchedArena(args[1], player.getUniqueId()) != null) {
                        Arena arena = arenaManager.getSearchedArena(args[1], player.getUniqueId());
                        if (args[0].equalsIgnoreCase("stop")) {
                            stopGame(arena, uuid);
                        }
                        if (args[0].equalsIgnoreCase("remove")) {
                            arenaManager.deleteArena(arena, player.getUniqueId());
                        }
                        if (args[0].equalsIgnoreCase("set")) {
                            arenaManager.set(player.getUniqueId(), arena);
                        }
                        if (args[0].equalsIgnoreCase("info")) {
                            info(arena, uuid);
                        }
                        if (args.length == 3) {
                            if (args[0].equalsIgnoreCase("start")) {
                                createGame(args[2], arena, uuid);
                            }
                            if (args[0].equalsIgnoreCase("modify")) {
                                modifyGame(args[2], arena, uuid);
                            }
                        }
                    } else if (arenaManager.getSearchedArena(args[1], player.getUniqueId()) == null) {
                        Message.ARENA_NOT_FOUND.send(Objects.requireNonNull(Bukkit.getPlayer(uuid)));
                    }
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        String argsIndex = "";

        /* Suggest child commands */
        if (args.length == 1) {
            argsIndex = args[0];

            suggestions.addAll(this.commandRegistry);
        }

        if (args.length == 2) {
            argsIndex = args[1];

        }

        return NameUtil.filterByStart(suggestions, argsIndex);
    }

    private void createGame(String value, Arena arena, UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        assert player != null;
        try {
            int numberOfPlayers = Integer.parseInt(value);
            if (numberOfPlayers % 2 == 0 && numberOfPlayers > 1) {
                if (arenaManager.locationsSet(arena)) {
                    if (!gameManager.isArenaBusy(arena)) {
                        gameManager.startNewGame(arena, numberOfPlayers);
                        Game game = gameManager.getGameFromArena(arena);
                        gameManager.message();
                        gameManager.getGamesGui().createNewGame(arena, game);
                    } else {
                        Message.GAME_ARENABUSY.send(player);
                    }
                } else {
                    Message.GAME_SETPOINTS.send(player);
                }
            } else {
                Message.GAME_ODDPLAYERS.send(player);
            }
        } catch (NumberFormatException exception) {
            Message.GAME_VALUE.send(player);
        }
    }

    private void stopGame(Arena arena, UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        assert player != null;
        if (gameManager.isArenaBusy(arena)) {
            Game game = gameManager.getGameFromArena(arena);
            game.teleportPlayersInPreviouslyLocation();
            game.clearEachPlayer();
            game.getScoreboard().removeScoreboards();
            gameManager.getGamesGui().removeGame(game);
            gameManager.removeGame(game);
            Message.GAME_STOPPED.send(player);
        } else {
            Message.GAME_ARENAFREE.send(player);
        }
    }

    private void modifyGame(String value, Arena arena, UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        assert player != null;
        if (gameManager.isArenaBusy(arena)) {
            Game game = gameManager.getGameFromArena(arena);
            try {
                int numberOfPlayers = Integer.parseInt(value);
                if (!game.isStarted()) {
                    if (numberOfPlayers % 2 == 0 && numberOfPlayers > 1) {
                        if (game.getPlayersPlaying().size() <= numberOfPlayers) {
                            if (!(game.getRed().getPlayersRoles().size() + game.getBlue().getPlayersRoles().size() == game.getNumberOfPlayers())) {
                                Message.GAME_EDITABLE.send(player);
                                game.setNumberOfPlayers(numberOfPlayers);
                                game.loadRolesRemaining();
                                game.getTeamGui().updateGui();
                                game.initializeRoleGui();
                                gameManager.getGamesGui().modifyGame(game);
                                if (game.isReadyToStart()) {
                                    Message.GAME_ISSTARTING.broadcast(game);
                                    game.start();
                                }
                            } else {
                                Message.GAME_UNMODIFIABLE2.send(player);
                            }
                        } else {
                            Message.GAME_UNMODIFIABLE.send(player);
                        }
                    } else {
                        Message.GAME_ODDPLAYERS.send(player);
                    }
                } else {
                    Message.GAME_STARTED.send(player);
                }
            } catch (NumberFormatException exception) {
                Message.GAME_VALUE.send(player);
            }
        } else {
            Message.GAME_ARENAFREE.send(player);
        }
    }

    private void info(Arena arena, UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        assert player != null;
        if (gameManager.isArenaBusy(arena)) {
            Game game = gameManager.getGameFromArena(arena);
            player.sendMessage(ChatColor.GREEN + "Nome partita: " + arena.getName());
            player.sendMessage(ChatColor.GREEN + "Player attuali: " + game.getPlayersPlaying().size());
            player.sendMessage(ChatColor.GREEN + "Player massimi: " + game.getNumberOfPlayers());
        } else {
            Message.GAME_ARENAFREE.send(player);
        }
    }
}
