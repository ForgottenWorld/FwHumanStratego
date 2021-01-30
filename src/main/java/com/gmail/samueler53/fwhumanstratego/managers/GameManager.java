package com.gmail.samueler53.fwhumanstratego.managers;

import com.gmail.samueler53.fwhumanstratego.gui.GamesGui;
import com.gmail.samueler53.fwhumanstratego.objects.Arena;
import com.gmail.samueler53.fwhumanstratego.objects.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GameManager {
    private static GameManager instance;
    List<Game> games = new ArrayList<>();
    GamesGui gamesGui = new GamesGui();

    private GameManager() {
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public void startNewGame(Arena arena, int numberOfPlayers) {
        games.add(new Game(arena, numberOfPlayers));
    }

    public boolean isArenaBusy(Arena arena) {
        return games.stream().anyMatch(Game -> Game.getArena().equals(arena));
    }

    public Optional<Game> isPlayerPlaying(UUID uuid) {
        return games.stream().filter(Game -> Game.isPlayerPlaying(uuid)).findFirst();
    }

    public boolean areInTheSameGame(UUID uuid1, UUID uuid2) {
        if (isPlayerPlaying(uuid1).isPresent() && isPlayerPlaying(uuid2).isPresent()) {
            return getGameWherePlayerPlaying(uuid1).equals(getGameWherePlayerPlaying(uuid2));
        } else {
            return false;
        }
    }

    public Game getGameWherePlayerPlaying(UUID uuid) {
        if (games.stream().anyMatch(Game -> Game.isPlayerPlaying(uuid))) {
            return games.stream().filter(Game -> Game.isPlayerPlaying(uuid)).findFirst().get();
        } else {
            return null;
        }
    }

    public Game getGameFromArena(Arena arena) {
        if (games.stream().anyMatch(Game -> Game.getArena().equals(arena))) {
            return games.stream().filter(Game -> Game.getArena().equals(arena)).findFirst().get();
        } else {
            return null;
        }
    }

    public void removeGame(Game game) {
        if (games.stream().anyMatch(Game -> Game.equals(game))) {
            games.remove(game);
        }
    }


    public GamesGui getGamesGui() {
        return gamesGui;
    }
}
