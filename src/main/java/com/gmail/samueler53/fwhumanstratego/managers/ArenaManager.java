package com.gmail.samueler53.fwhumanstratego.managers;

import com.github.stefvanschie.inventoryframework.Gui;
import com.gmail.samueler53.fwhumanstratego.FwHumanStratego;
import com.gmail.samueler53.fwhumanstratego.gui.SetArenaGui;
import com.gmail.samueler53.fwhumanstratego.message.Message;
import com.gmail.samueler53.fwhumanstratego.objects.Arena;
import com.gmail.samueler53.fwhumanstratego.objects.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class ArenaManager {
    //DA SISTEMARE I VARI TELEPORT. ESSI DEVONO SOLAMENTE TELETRASPORTARE E BASTA.
    //DEVONO ESSERE LA LISTA DELLE ARENE OCCUPATE E LA GESTIONE DELLA LANA

    private static ArenaManager instance;

    private final List<Arena> arenas;

    private ArenaManager() {
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        arenas = FwHumanStratego.getData().getArene();
    }

    public static ArenaManager getInstance() {
        if (instance == null) {
            instance = new ArenaManager();
        }
        return instance;
    }

    public void createArena(String name, UUID uuid) {
        if (arenas.stream().noneMatch(Arena -> Arena.getName().equalsIgnoreCase(name))) {
            arenas.add(new Arena(name));
            Message.ARENA_CREATED.send(Objects.requireNonNull(Bukkit.getPlayer(uuid)), name);
            FwHumanStratego.getData().setArene(arenas);
        } else {
            Message.ARENA_ALREADY_EXISTS.send(Objects.requireNonNull(Bukkit.getPlayer(uuid)), name);
        }
    }

    public void removeChest(Location location) {
        if (location != null && location.getBlock().getType() == Material.CHEST) {
            clearChest(location.getBlock());
            location.getBlock().setType(Material.AIR);
        }
    }

    public void clearChest(Block block) {
        org.bukkit.block.Chest chest = (org.bukkit.block.Chest) block.getState();
        chest.getBlockInventory().clear();
    }

    public void deleteArena(Arena arena, UUID uuid) {
        removeChest(arena.getRedTeamLocation());
        removeChest(arena.getBlueTeamLocation());
        arenas.remove(arena);
        Message.ARENA_REMOVE.send(Objects.requireNonNull(Bukkit.getPlayer(uuid)));
        FwHumanStratego.getData().setArene(arenas);
    }

    public void teleportSquads(Arena arena, Game game) {
        teleportRedSquad(arena, game);
        teleportBlueSquad(arena, game);
    }

    public void teleportRedSquad(Arena arena, Game game) {
        for (UUID uuid : game.getRed().getPlayersRoles().keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;
            player.teleport(arena.getRedTeamLocation());
        }
    }

    public void teleportRedPlayer(UUID uuid, Game game) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        player.teleport(game.getArena().getRedTeamLocation());

    }

    public void teleportBlueSquad(Arena arena, Game game) {
        for (UUID uuid : game.getBlue().getPlayersRoles().keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;
            player.teleport(arena.getBlueTeamLocation());
        }
    }

    public void teleportBluePlayer(UUID uuid, Game game) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        player.teleport(game.getArena().getBlueTeamLocation());

    }


    public void teleportPlayerToLobby(UUID uuid, Arena arena) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        player.teleport(arena.getLobbyLocation());
    }

    public void teleportPlayerToHisSpawnPoint(UUID uuid, Game game) {
        if (game.getSquadFromPlayer(uuid).getName().equalsIgnoreCase("red")) {
            teleportRedPlayer(uuid, game);
        } else {
            teleportBluePlayer(uuid, game);
        }
    }

    public void initializeArena(Arena arena) {
        treasureRed(arena.getTreasureRedLocation(), arena);
        treasureBlue(arena.getTreasureBlueLocation(), arena);
    }

    public void treasureRed(Location location, Arena arena) {
        removeChest(arena.getTreasureRedLocation());
        if (location == null) return;
        location.getBlock().setType(Material.CHEST);
        Block block = location.getBlock();
        if (block.getType() != Material.CHEST) {
            return;
        }
        org.bukkit.block.Chest chest = (org.bukkit.block.Chest) block.getState();
        chest.getBlockInventory().addItem(new ItemStack(Material.RED_WOOL));
    }

    public void treasureBlue(Location location, Arena arena) {
        removeChest(arena.getTreasureBlueLocation());
        if (location == null) return;
        location.getBlock().setType(Material.CHEST);
        Block block = location.getBlock();
        if (block.getType() != Material.CHEST) {
            return;
        }
        org.bukkit.block.Chest chest = (org.bukkit.block.Chest) block.getState();
        chest.getBlockInventory().addItem(new ItemStack(Material.BLUE_WOOL));
    }

    public boolean locationsSet(Arena arena) {
        return arena.getRedTeamLocation() != null && arena.getBlueTeamLocation() != null && arena.getTreasureRedLocation() != null && arena.getTreasureBlueLocation() != null && arena.getLobbyLocation() != null;
    }

    public Optional<Arena> isPresentArena(String arenaName) {
        return arenas.stream().filter(arena -> arena.getName().equals(arenaName)).findFirst();
    }

    public void setTreasureRedLocation(Location treasureRedLocation, Arena arena, UUID uuid) {
        treasureRed(treasureRedLocation, arena);
        int x = treasureRedLocation.getBlockX();
        int y = treasureRedLocation.getBlockY();
        int z = treasureRedLocation.getBlockZ();
        arena.setTreasureRedLocation(new Location(treasureRedLocation.getWorld(), x, y, z));
        Message.ARENA_CREATION_TREASURERED.send(Objects.requireNonNull(Bukkit.getPlayer(uuid)));
        FwHumanStratego.getData().setArene(arenas);
        Objects.requireNonNull(Bukkit.getPlayer(uuid)).closeInventory();
    }

    public void setTreasureBlueLocation(Location treasureBlueLocation, Arena arena, UUID uuid) {
        treasureBlue(treasureBlueLocation, arena);
        int x = treasureBlueLocation.getBlockX();
        int y = treasureBlueLocation.getBlockY();
        int z = treasureBlueLocation.getBlockZ();
        arena.setTreasureBlueLocation(new Location(treasureBlueLocation.getWorld(), x, y, z));
        Message.ARENA_CREATION_TREASUREBLUE.send(Objects.requireNonNull(Bukkit.getPlayer(uuid)));
        FwHumanStratego.getData().setArene(arenas);
        Objects.requireNonNull(Bukkit.getPlayer(uuid)).closeInventory();
    }

    public void setRedTeamLocation(Location redTeamLocation, Arena arena, UUID uuid) {
        arena.setRedTeamLocation(redTeamLocation);
        Message.ARENA_CREATION_TEAMRED.send(Objects.requireNonNull(Bukkit.getPlayer(uuid)));
        FwHumanStratego.getData().setArene(arenas);
        Objects.requireNonNull(Bukkit.getPlayer(uuid)).closeInventory();
    }

    public void setBlueTeamLocation(Location blueTeamLocation, Arena arena, UUID uuid) {
        arena.setBlueTeamLocation(blueTeamLocation);
        Message.ARENA_CREATION_TEAMBLUE.send(Objects.requireNonNull(Bukkit.getPlayer(uuid)));
        FwHumanStratego.getData().setArene(arenas);
        Objects.requireNonNull(Bukkit.getPlayer(uuid)).closeInventory();
    }

    public void setLobbyLocation(Location lobbyLocation, Arena arena, UUID uuid) {
        arena.setLobbyLocation(lobbyLocation);
        Message.ARENA_CREATION_LOBBY.send(Objects.requireNonNull(Bukkit.getPlayer(uuid)));
        FwHumanStratego.getData().setArene(arenas);
        Objects.requireNonNull(Bukkit.getPlayer(uuid)).closeInventory();
    }

    public void set(UUID uuid, Arena arena) {
        Player p = Bukkit.getPlayer(uuid);
        SetArenaGui instanceGui = SetArenaGui.getInstance();
        if (p == null) return;
        instanceGui.setPlayer(p.getUniqueId());
        instanceGui.setArena(arena);
        Gui gui = instanceGui.SetGui();
        gui.show(p);
    }

    public Arena getSearchedArena(String arenaName) {
        return isPresentArena(arenaName).orElse(null);
    }

    public void getArenas(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        for (Arena arena : arenas) {
            if (p == null) return;
            Message.ARENA_LIST.send(p, arena.getName());
        }
    }
}
