package com.gmail.samueler53.fwhumanstratego.gui;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.gmail.samueler53.fwhumanstratego.managers.ArenaManager;
import com.gmail.samueler53.fwhumanstratego.message.Message;
import com.gmail.samueler53.fwhumanstratego.objects.Arena;
import com.gmail.samueler53.fwhumanstratego.objects.Game;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;
import java.util.UUID;

public class GamesGui {
    private Gui mainGui;
    private final ArenaManager arenaManager = ArenaManager.getInstance();


    public GamesGui() {
        mainGui = prepareGui();
    }

    public Gui prepareGui() {
        mainGui = new Gui(3, "Games");
        mainGui.setOnGlobalClick(event -> event.setCancelled(true));

        OutlinePane background = new OutlinePane(0, 0, 9, 3);
        background.addItem(new GuiItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)));
        background.setRepeat(true);
        mainGui.addPane(background);

        OutlinePane gamesPane = new OutlinePane(0, 0, 9, 1);
        mainGui.addPane(gamesPane);

        return mainGui;
    }

    public void createNewGame(Arena arena, Game game) {
        ItemStack itemStack = new ItemStack(Material.NETHERITE_BLOCK);
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(arena.getName());
        itemStack.setItemMeta(itemMeta);
        addGame(itemStack, game);
    }

    private void addGame(ItemStack itemStack, Game game) {
        OutlinePane gamesPane = (OutlinePane) mainGui.getPanes().get(1);
        mainGui.setOnOutsideClick(event -> event.setCancelled(true));
        gamesPane.addItem(new GuiItem(itemStack, event -> addAPlayer(event.getWhoClicked().getUniqueId(), game)));
        mainGui.addPane(gamesPane);
        mainGui.update();
    }

    public void removeGame(Game game) {
        mainGui.getPanes().get(1).getItems().removeIf(GuiItem -> Objects.requireNonNull(GuiItem.getItem().getItemMeta()).getDisplayName().equalsIgnoreCase(game.getArena().getName()));
        mainGui.update();
    }

    public void show(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        assert player != null;
        mainGui.show(player);
    }

    private void addAPlayer(UUID uuid, Game game) {
        Player player = Bukkit.getPlayer(uuid);
        assert player != null;
        if (game.getNumberOfPlayers() != game.getPlayersPlaying().size()) {
            Message.GAME_JOIN.send(player);
            game.addAPlayer(uuid);
            game.getPlayersLocations().put(uuid, player.getLocation());
            arenaManager.teleportPlayerToLobby(uuid, game.getArena());
            game.clearPlayer(uuid);
            player.closeInventory();
        } else {
            Message.GAME_GAMEFULL.send(player);
        }
    }

}
