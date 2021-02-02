package com.gmail.samueler53.fwhumanstratego.managers;

import com.gmail.samueler53.fwhumanstratego.gui.GamesGui;
import com.gmail.samueler53.fwhumanstratego.objects.Arena;
import com.gmail.samueler53.fwhumanstratego.objects.Game;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

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

    public void message() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String startMessage = ChatColor.GREEN + String.join("", Collections.nCopies(53, "-")) + ChatColor.DARK_AQUA + "Ciao " + player.getName() + ", se vuoi giocare a HumanStratego, clicca sotto";
            TextComponent clickMessage = new TextComponent("\n\n [Clicca qui per giocare]\n");
            clickMessage.setColor(ChatColor.GREEN);
            clickMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hs join"));
            clickMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Clicca per giocare")));
            String endMessage = ChatColor.GREEN + String.join("", Collections.nCopies(53, "-"));
            ComponentBuilder message = new ComponentBuilder();
            message
                    .append(startMessage)
                    .append(clickMessage)
                    .append(endMessage);
            player.spigot().sendMessage(message.create());
        }
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
        return games.stream().filter(Game -> Game.isPlayerPlaying(uuid)).findFirst().orElse(null);
    }

    public Game getGameFromArena(Arena arena) {
        return games.stream().filter(Game -> Game.getArena().equals(arena)).findFirst().orElse(null);
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
