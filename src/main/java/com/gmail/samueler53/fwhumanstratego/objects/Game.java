package com.gmail.samueler53.fwhumanstratego.objects;

import com.gmail.samueler53.fwhumanstratego.FwHumanStratego;
import com.gmail.samueler53.fwhumanstratego.gui.TeamGui;
import com.gmail.samueler53.fwhumanstratego.managers.ArenaManager;
import com.gmail.samueler53.fwhumanstratego.managers.GameManager;
import com.gmail.samueler53.fwhumanstratego.message.Message;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Game {
    private final Arena arena;
    private int numberOfPlayers;
    private final List<UUID> playersPlaying = new ArrayList<>();
    private final List<Role> roles = new ArrayList<>();
    private final Map<UUID, Location> playersLocations = new HashMap<>();
    private boolean started = false;
    private int rounds = 0;
    private final Squad red = new Squad("red", Color.RED, Material.RED_WOOL, this);
    private final Squad blue = new Squad("blue", Color.BLUE, Material.BLUE_WOOL, this);
    private final ArenaManager arenaManager = ArenaManager.getInstance();
    private final GameManager gameManager = GameManager.getInstance();
    private final Scoreboard scoreboard = new Scoreboard(this);
    private final TeamGui teamGui;
    final FileConfiguration config = FwHumanStratego.getDefaultConfig();


    public Game(Arena arena, int numberOfPlayers) {
        this.arena = arena;
        this.numberOfPlayers = numberOfPlayers;
        loadDefaultRoles();
        loadRolesRemaining();
        teamGui = new TeamGui(this);
    }

    public void loadDefaultRoles() {
        for (String roleName : Objects.requireNonNull(config.getConfigurationSection("roles")).getKeys(false)) {
            int points = config.getInt("roles." + roleName + ".points");
            String description = config.getString("roles." + roleName + ".description");
            this.roles.add(new Role(roleName, points, description));
        }
    }

    public void loadRolesRemaining() {
        Map<Role, Integer> rolesRemaining = new HashMap<>();
        addValueForEachRole(rolesRemaining);
        List<Role> specialRolesList = loadSpecialRoles();
        int specialRoles = numberOfPlayers / 2 - 1;


        for (Role role : specialRolesList) {
            if (role.getName().equalsIgnoreCase("generale")) {
                rolesRemaining.put(role, 1);
                break;
            }
        }
        while (specialRoles > 0) {
            for (Role role : specialRolesList) {
                if (specialRoles > 0 && !role.getName().equalsIgnoreCase("generale")) {
                    rolesRemaining.put(role, rolesRemaining.get(role) + 1);
                    specialRoles--;
                }
            }
        }
        List<Role> normalRolesList = loadNormalRoles();
        int normalroles = numberOfPlayers;
        while (normalroles > 0) {
            for (Role role : normalRolesList) {
                if (normalroles > 0) {
                    rolesRemaining.put(role, rolesRemaining.get(role) + 1);
                    normalroles--;
                }
            }
        }
        red.setRolesRemaining(rolesRemaining);
        blue.setRolesRemaining(rolesRemaining);
    }

    private void addValueForEachRole(Map<Role, Integer> rolesRemaining) {
        for (Role role : roles) {
            rolesRemaining.put(role, 0);
        }
    }


    public List<Role> loadSpecialRoles() {
        List<Role> specialRoles = new ArrayList<>();
        for (Role role : roles) {
            if (isASpecialRole(role)) {
                specialRoles.add(role);
            }
        }
        return specialRoles;
    }

    public List<Role> loadNormalRoles() {
        List<Role> normalRoles = new ArrayList<>();
        for (Role role : roles) {
            if (isANormalRole(role)) {
                normalRoles.add(role);
            }
        }
        return normalRoles;
    }

    public void messageRole(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        TextComponent clickMessage = new TextComponent("[INFORMAZIONI SUL RUOLO]");
        clickMessage.setColor(ChatColor.GREEN);
        clickMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hs info " + getRoleFromPlayer(uuid).getName()));
        clickMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Clicca per avere informazioni sul tuo ruolo")));
        if (player == null) {
            return;
        }
        player.spigot().sendMessage(clickMessage);
    }

    public void addAPlayer(UUID uuid) {
        if (!isPlayerPlaying(uuid)) {
            playersPlaying.add(uuid);
        }
    }

    public void removeAPlayer(UUID uuid) {
        if (isPlayerPlaying(uuid)) {
            playersPlaying.remove(uuid);
        }
    }

    public void squadAssignment(UUID uuid, Squad squad) {
        squad.addPlayer(uuid);
    }

    public void start() {
        Game game = this;
        Bukkit.getScheduler().runTaskLater(FwHumanStratego.getPlugin(), () -> {
            started = true;
            rounds++;
            initializeRoleGui();
            survivalModeEachPlayer();
            arenaManager.teleportSquads(arena, game);
            equipKits();
            scoreboard.initScoreboards();
            arenaManager.initializeArena(arena);
            gameManager.getGamesGui().removeGame(game);
        }, config.getInt("delayStartGame"));
    }

    public void equipKits() {
        red.equipKitForEachPlayer();
        blue.equipKitForEachPlayer();
    }

    public void initializeRoleGui() {
        red.getRoleGui().addItemStack();
        blue.getRoleGui().addItemStack();
    }

    public void endRound(Squad squad) {
        Message.GAME_ROUNDWINNER.broadcast(this, squad.getName());
        scoreboard.removeScoreboards();
        clearEachPlayer();
        clearRoles();
        if (isEndGame()) {
            endGame();
        } else {
            spectatorModeEachPlayer();
            loadRolesRemaining();
            Message.GAME_NEWROUND.broadcast(this);
            start();
        }
    }

    public void endGame() {
        started = false;
        if (isDraw()) {
            Message.GAME_DRAW.broadcast(this);
        } else {
            Squad squadWinner = getSquadWinner();
            Message.GAME_TEAMWINNER.broadcast(this, squadWinner.getName());
        }
        survivalModeEachPlayer();
        teleportPlayersInPreviouslyLocation();
        gameManager.removeGame(this);
    }

    public void teleportPlayersInPreviouslyLocation() {
        for (UUID uuid : playersLocations.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                return;
            }
            player.teleport(playersLocations.get(uuid));
        }
    }

    public void addPlayerRole(UUID uuid, Role role, Squad squad) {
        squad.addPlayerRole(uuid, role);
    }

    private void clearRoles() {
        red.clearRoles();
        blue.clearRoles();
    }

    public void clearEachPlayer() {
        for (UUID uuid : playersPlaying) {
            clearPlayer(uuid);
        }
    }

    private void survivalModeEachPlayer() {
        for (UUID uuid : playersPlaying) {
            survivalMode(uuid);
        }
    }

    private void spectatorModeEachPlayer() {
        for (UUID uuid : playersPlaying) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                return;
            }
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    public void clearPlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }
        player.setFoodLevel(20);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        survivalMode(uuid);
        if (Objects.requireNonNull(Bukkit.getPlayer(uuid)).getWalkSpeed() != 0.2F) {
            Objects.requireNonNull(Bukkit.getPlayer(uuid)).setWalkSpeed(0.2F);
        }
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
    }

    private void survivalMode(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }
        if (player.getGameMode() != GameMode.SURVIVAL) {
            player.setGameMode(GameMode.SURVIVAL);
        }
    }


    public void stolenWool(UUID uuid) {
        Squad otherSquad = getOtherSquad(getSquadFromPlayer(uuid));
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }
        if (otherSquad.getTreasure() == Material.BLUE_WOOL) {
            player.getInventory().remove(Material.BLUE_WOOL);
            arenaManager.treasureBlue(arena.getTreasureBlueLocation(), arena);
        } else if (otherSquad.getTreasure() == Material.RED_WOOL) {
            player.getInventory().remove(Material.RED_WOOL);
            arenaManager.treasureRed(arena.getTreasureRedLocation(), arena);
        }
    }

    public void spectateMode(Squad squad) {
        for (UUID uuid : squad.getPlayersRoles().keySet()) {
            if (!hasARole(uuid)) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) {
                    return;
                }
                player.closeInventory();
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
    }

    public Boolean canKill(Role roleDamageDealer, Role roleDamaged) {
        String value = config.getString("roles." + roleDamageDealer.getName() + ".can_kill");
        if (value == null) return false;
        String[] s = value.split(",");
        return Arrays.stream(s).anyMatch(String -> String.equalsIgnoreCase(roleDamaged.getName()));
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public boolean areInTheSameTeam(UUID uuid1, UUID uuid2) {
        return getSquadFromPlayer(uuid1).equals(getSquadFromPlayer(uuid2));
    }

    public boolean hasASquad(UUID uuid) {
        return red.getPlayersRoles().containsKey(uuid) || blue.getPlayersRoles().containsKey(uuid);
    }

    public boolean hasARole(UUID uuid) {
        Squad squad = getSquadFromPlayer(uuid);
        return squad.getPlayersRoles().get(uuid) != null;
    }

    public boolean hasStoleWool(UUID uuid) {
        Squad otherSquad = getOtherSquad(getSquadFromPlayer(uuid));
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return false;
        return player.getInventory().contains(otherSquad.getTreasure());
    }

    public boolean isPlayerPlaying(UUID uuid) {
        return playersPlaying.contains(uuid);
    }

    public boolean isReadyToStart() {
        return numberOfPlayers / 2 == red.getPlayersRoles().size() &&
                numberOfPlayers / 2 == blue.getPlayersRoles().size();
    }

    private boolean isASpecialRole(Role role) {
        return role.getName().equalsIgnoreCase("generale") ||
                role.getName().equalsIgnoreCase("assassino") ||
                role.getName().equalsIgnoreCase("bomba") ||
                role.getName().equalsIgnoreCase("artificiere");
    }

    private boolean isANormalRole(Role role) {
        return role.getName().equalsIgnoreCase("maresciallo") ||
                role.getName().equalsIgnoreCase("colonnello") ||
                role.getName().equalsIgnoreCase("maggiore");
    }

    public boolean isAbleToUse(Role role, Squad squad) {
        return getPlayerWhoHaveThisRole(role, squad) < config.getInt("roles." + role.getName() + ".max_players");
    }

    public boolean isRemainingARole(Role role, Squad squad) {
        return squad.getRolesRemaining().get(role) > 0;
    }

    public boolean isGeneral(UUID uuid) {
        return getRoleFromPlayer(uuid).getName().equalsIgnoreCase("Generale");
    }

    public boolean isBomb(UUID uuid) {
        return getRoleFromPlayer(uuid).getName().equalsIgnoreCase("Bomba");
    }

    public boolean isStarted() {
        return started;
    }

    private boolean isEndGame() {
        return rounds == config.getInt("rounds");
    }

    public boolean isSpectateMode(Squad squad) {
        for (Integer value : squad.getRolesRemaining().values()) {
            if (value != 0) {
                return false;
            }
        }
        return true;
    }

    private boolean isDraw() {
        return red.getPoints() == blue.getPoints();
    }

    public Arena getArena() {
        return arena;
    }

    public Squad getRed() {
        return red;
    }

    public Squad getBlue() {
        return blue;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public Squad getOtherSquad(Squad squad) {
        if (squad.equals(red)) {
            return blue;
        } else {
            return red;
        }
    }

    public List<UUID> getPlayersPlaying() {
        return playersPlaying;
    }

    public Role getRoleFromPlayer(UUID uuid) {
        return getSquadFromPlayer(uuid).getPlayersRoles().get(uuid);
    }

    @Nullable
    public Role getRoleByName(@NotNull String roleName) {
        return roles.stream()
                .filter(r -> r.name.equals(roleName.toLowerCase(Locale.ROOT)))
                .findFirst()
                .orElse(null);
    }

    public int getPlayerWhoHaveThisRole(Role role, Squad squad) {
        int counter = 0;
        for (Map.Entry<UUID, Role> entry : squad.getPlayersRoles().entrySet()) {
            if (entry.getValue() != null && entry.getValue().equals(role)) {
                counter++;
            }
        }
        return counter;
    }

    public Squad getSquadFromPlayer(UUID uuid) {
        return red.getPlayersRoles().containsKey(uuid) ? red : blue;
    }

    public UUID getWhoLose(UUID uuidDamageDealer, UUID uuidDamaged) {
        Role roleDamageDealer = getRoleFromPlayer(uuidDamageDealer);
        Role roleDamaged = getRoleFromPlayer(uuidDamaged);
        return canKill(roleDamageDealer, roleDamaged) ? uuidDamaged : uuidDamageDealer;

    }

    public UUID getWhoWin(UUID uuidDamageDealer, UUID uuidDamaged) {
        Role roleDamageDealer = getRoleFromPlayer(uuidDamageDealer);
        Role roleDamaged = getRoleFromPlayer(uuidDamaged);
        return canKill(roleDamageDealer, roleDamaged) ? uuidDamageDealer : uuidDamaged;
    }

    private Squad getSquadWinner() {
        return red.getPoints() > blue.getPoints() ? red : blue;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public TeamGui getTeamGui() {
        return teamGui;
    }

    public Map<UUID, Location> getPlayersLocations() {
        return playersLocations;
    }
}
