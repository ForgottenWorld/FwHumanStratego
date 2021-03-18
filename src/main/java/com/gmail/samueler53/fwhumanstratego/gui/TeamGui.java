package com.gmail.samueler53.fwhumanstratego.gui;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.gmail.samueler53.fwhumanstratego.message.Message;
import com.gmail.samueler53.fwhumanstratego.objects.Game;
import com.gmail.samueler53.fwhumanstratego.objects.Squad;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class TeamGui {

    private Gui mainGui;
    private final Game game;

    public TeamGui(Game game) {
        this.game = game;
        mainGui = prepareGui();
    }

    public Gui prepareGui() {
        mainGui = new Gui(3, "TeamsGui");
        mainGui.setOnGlobalClick(event -> event.setCancelled(true));

        OutlinePane background = new OutlinePane(0, 0, 9, 3);
        background.addItem(new GuiItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)));
        background.setRepeat(true);
        mainGui.addPane(background);


        addItemStack();

        return mainGui;
    }

    public void addItemStack() {
        OutlinePane redTeamPane = new OutlinePane(3, 1, 1, 1);
        OutlinePane blueTeamPane = new OutlinePane(5, 1, 6, 1);
        ItemStack redTeamItemStack = redTeamItemStack();
        ItemStack blueTeamItemStack = blueTeamItemStack();
        mainGui.setOnOutsideClick(event -> event.setCancelled(true));
        redTeamPane.addItem(new GuiItem(redTeamItemStack, event -> chooseTeam(event.getWhoClicked().getUniqueId(), game.getRed())));
        blueTeamPane.addItem(new GuiItem(blueTeamItemStack, event -> chooseTeam(event.getWhoClicked().getUniqueId(), game.getBlue())));
        mainGui.addPane(redTeamPane);
        mainGui.addPane(blueTeamPane);
    }

    private ItemStack redTeamItemStack() {
        ItemStack redTeamStack = new ItemStack(Material.RED_WOOL);
        ItemMeta redTeamMeta = redTeamStack.getItemMeta();
        if (redTeamMeta == null) return redTeamStack;
        redTeamMeta.setDisplayName("Team Rosso " + game.getRed().getPlayersRoles().size() + "/" + game.getNumberOfPlayers() / 2);
        redTeamStack.setItemMeta(redTeamMeta);
        return redTeamStack;
    }

    private ItemStack blueTeamItemStack() {
        ItemStack blueTeamStack = new ItemStack(Material.BLUE_WOOL);
        ItemMeta blueTeamMeta = blueTeamStack.getItemMeta();
        if (blueTeamMeta == null) return blueTeamStack;
        blueTeamMeta.setDisplayName("Team Blu " + game.getBlue().getPlayersRoles().size() + "/" + game.getNumberOfPlayers() / 2);
        blueTeamStack.setItemMeta(blueTeamMeta);

        return blueTeamStack;
    }

    public void updateGui() {
        addItemStack();
        mainGui.update();
    }

    public void show(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        mainGui.show(player);
    }

    public Inventory getInventory() {
        return mainGui.getInventory();
    }

    public void chooseTeam(UUID uuid, Squad squad) {
        Squad otherSquad = game.getOtherSquad(squad);
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        if (squad.getPlayersRoles().size() == game.getNumberOfPlayers()/2) {
            Message.GAME_TEAMFULL.send(player);
            return;
        } else if (squad.getPlayersRoles().containsKey(uuid)) {
            squad.removePlayer(uuid);
        } else if (otherSquad.getPlayersRoles().containsKey(uuid)) {
            otherSquad.removePlayer(uuid);
        }
        game.squadAssignment(uuid, squad);
        this.updateGui();
        player.closeInventory();
        if (squad.getName().equalsIgnoreCase("red")) {
            Message.GAME_TEAMRED.send(player);
        } else if (squad.getName().equalsIgnoreCase("blue")) {
            Message.GAME_TEAMBLUE.send(player);
        }
        if (game.isReadyToStart()) {
            Message.GAME_ISSTARTING.broadcast(game);
            game.start();
        }

    }

}
