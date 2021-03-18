package com.gmail.samueler53.fwhumanstratego.objects;


import com.gmail.samueler53.fwhumanstratego.utils.FastBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class Scoreboard {

    // private static Scoreboard instance;
    private final Map<UUID, FastBoard> boards;
    final Game game;

    Scoreboard(Game game) {
//        if (instance != null) {
//            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
//        }
        this.boards = new HashMap<>();
        this.game = game;
    }

    public void initScoreboards() {
        for (UUID uuid : game.getPlayersPlaying()) {
            Player player = Bukkit.getPlayer(uuid);
            FastBoard board = new FastBoard(player);
            board.updateTitle(ChatColor.DARK_GRAY + "[" +
                    ChatColor.RED + "Fw" +
                    ChatColor.WHITE + "HumanStratego" +
                    ChatColor.DARK_GRAY + "]");
            if (player == null) return;
            this.boards.put(player.getUniqueId(), board);
        }
    }

    public void removeScoreboards() {
        Iterator<UUID> boardsIterator = this.boards.keySet().iterator();
        List<UUID> boardsToRemove = new ArrayList<>();
        while (boardsIterator.hasNext()) {
            UUID currentUUID = boardsIterator.next();
            Player player = Bukkit.getPlayer(currentUUID);
            if (player != null) {
                FastBoard board = this.boards.get(currentUUID);
                board.delete();
                boardsIterator.remove();
                boardsToRemove.add(currentUUID);
            }
        }
        for (UUID boardToRemove : boardsToRemove) {
            this.boards.remove(boardToRemove);
        }
    }

    public void removeScoreboardForPlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            if (this.boards.get(player.getUniqueId()).size() != 0) {
                FastBoard board = this.boards.get(uuid);
                for(int i = 0; i<board.size();i++){
                    board.removeLine(i);
                }
            }
        }
    }

    public void updatePlayerRole(UUID uuid) {
        removeScoreboardForPlayer(uuid);
        FastBoard board = this.boards.get(uuid);
        List<String> lines = new ArrayList<>();
        lines.add("");
        lines.add(ChatColor.RED + "Role: " + ChatColor.WHITE + game.getRoleFromPlayer(uuid).getName());
        lines.add("");
        board.updateLines(lines);
    }
}
