package com.gmail.samueler53.fwhumanstratego.gui;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.gmail.samueler53.fwhumanstratego.managers.ArenaManager;
import com.gmail.samueler53.fwhumanstratego.objects.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Objects;
import java.util.UUID;

public class SetArenaGui {


    private final Gui setGui = new Gui(3, "Role");
    private UUID u;
    private Arena arena;

    public static SetArenaGui getInstance() {
        return new SetArenaGui();
    }

    public Gui SetGui() {

        setGui.setOnGlobalClick(event -> event.setCancelled(true));

        OutlinePane background = new OutlinePane(0, 0, 9, 3);
        background.addItem(new GuiItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)));
        background.setRepeat(true);
        setGui.addPane(background);


        OutlinePane rolePane = new OutlinePane(2, 1, 5, 1);

        ItemStack redTeamStack = redTeamItemStack();
        ItemStack blueTeamStack = blueTeamItemStack();
        ItemStack treasureRedStack = treasureRedItemStack();
        ItemStack treasureBlueStack = treasureBlueItemStack();
        ItemStack lobbyStack = lobbyItemStack();
        ArenaManager instanceArena = ArenaManager.getInstance();


        rolePane.addItem(new GuiItem(redTeamStack, event -> instanceArena.setRedTeamLocation(Objects.requireNonNull(Bukkit.getPlayer(u)).getLocation(), arena, u)));
        rolePane.addItem(new GuiItem(blueTeamStack, event -> instanceArena.setBlueTeamLocation(Objects.requireNonNull(Bukkit.getPlayer(u)).getLocation(), arena, u)));
        rolePane.addItem(new GuiItem(treasureRedStack, event -> instanceArena.setTreasureRedLocation(Objects.requireNonNull(Bukkit.getPlayer(u)).getLocation(), arena, u)));
        rolePane.addItem(new GuiItem(treasureBlueStack, event -> instanceArena.setTreasureBlueLocation(Objects.requireNonNull(Bukkit.getPlayer(u)).getLocation(), arena, u)));
        rolePane.addItem(new GuiItem(lobbyStack, event -> instanceArena.setLobbyLocation(Objects.requireNonNull(Bukkit.getPlayer(u)).getLocation(), arena, u)));
        setGui.addPane(rolePane);

        return setGui;
    }

    public ItemStack getMaterial(Color color) {
        ItemStack LeatherChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta) LeatherChestplate.getItemMeta();
        assert meta != null;
        meta.setColor(color);
        LeatherChestplate.setItemMeta(meta);
        return LeatherChestplate;
    }

    private ItemStack redTeamItemStack() {
        ItemStack redTeamStack = getMaterial(Color.RED);
        ItemMeta redTeamMeta = redTeamStack.getItemMeta();
        assert redTeamMeta != null;
        redTeamMeta.setDisplayName("Spawn del team rosso");
        redTeamStack.setItemMeta(redTeamMeta);
        return redTeamStack;
    }

    private ItemStack blueTeamItemStack() {
        ItemStack blueTeamStack = getMaterial(Color.BLUE);
        ItemMeta blueTeamMeta = blueTeamStack.getItemMeta();
        assert blueTeamMeta != null;
        blueTeamMeta.setDisplayName("Spawn del team blu");
        blueTeamStack.setItemMeta(blueTeamMeta);
        return blueTeamStack;
    }

    private ItemStack treasureRedItemStack() {
        ItemStack treasureRedStack = new ItemStack(Material.RED_WOOL);
        ItemMeta treasureRedMeta = treasureRedStack.getItemMeta();
        assert treasureRedMeta != null;
        treasureRedMeta.setDisplayName("Tesoro rosso");
        treasureRedStack.setItemMeta(treasureRedMeta);
        return treasureRedStack;
    }

    private ItemStack treasureBlueItemStack() {
        ItemStack treasureBlueStack = new ItemStack(Material.BLUE_WOOL);
        ItemMeta treasureBlueMeta = treasureBlueStack.getItemMeta();
        assert treasureBlueMeta != null;
        treasureBlueMeta.setDisplayName("Tesoro blu");
        treasureBlueStack.setItemMeta(treasureBlueMeta);
        return treasureBlueStack;
    }

    private ItemStack lobbyItemStack() {
        ItemStack LobbyStack = new ItemStack(Material.GLASS_PANE);
        ItemMeta LobbyMeta = LobbyStack.getItemMeta();
        assert LobbyMeta != null;
        LobbyMeta.setDisplayName("Lobby");
        LobbyStack.setItemMeta(LobbyMeta);
        return LobbyStack;
    }

    public void setPlayer(UUID u) {
        this.u = u;
    }

    public void setArena(Arena arena) {
        this.arena = arena;
    }

}
