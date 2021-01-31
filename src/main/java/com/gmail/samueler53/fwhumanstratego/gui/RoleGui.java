package com.gmail.samueler53.fwhumanstratego.gui;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.gmail.samueler53.fwhumanstratego.FwHumanStratego;
import com.gmail.samueler53.fwhumanstratego.message.Message;
import com.gmail.samueler53.fwhumanstratego.objects.Game;
import com.gmail.samueler53.fwhumanstratego.objects.Role;
import com.gmail.samueler53.fwhumanstratego.objects.Squad;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class RoleGui {

    private Gui mainGui;
    Game game;
    Squad squad;

    public RoleGui(Squad squad, Game game) {
        this.game = game;
        this.squad = squad;
        mainGui = prepareGui();
    }

    public Gui prepareGui() {
        mainGui = new Gui(3, "Role");
        mainGui.setOnGlobalClick(event -> event.setCancelled(true));

        OutlinePane background = new OutlinePane(0, 0, 9, 3);
        background.addItem(new GuiItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)));
        background.setRepeat(true);
        mainGui.addPane(background);


        return mainGui;
    }

    public void addItemStack() {
        OutlinePane rolePane = new OutlinePane(1, 1, 7, 1);
        ItemStack generaleStack = generaleItemStack();
        ItemStack marescialloStack = marescialloItemStack();
        ItemStack colonnelloStack = colonnelloItemStack();
        ItemStack maggioreStack = maggioreItemStack();
        ItemStack artificiereStack = artificiereItemStack();
        ItemStack bombaStack = bombaItemStack();
        ItemStack assassinoStack = assassinoItemStack();
        mainGui.setOnOutsideClick(event -> event.setCancelled(true));
        rolePane.addItem(new GuiItem(generaleStack, event -> replaceRole(event.getWhoClicked().getUniqueId(), "generale")));
        rolePane.addItem(new GuiItem(marescialloStack, event -> replaceRole(event.getWhoClicked().getUniqueId(), "maresciallo")));
        rolePane.addItem(new GuiItem(colonnelloStack, event -> replaceRole(event.getWhoClicked().getUniqueId(), "colonnello")));
        rolePane.addItem(new GuiItem(maggioreStack, event -> replaceRole(event.getWhoClicked().getUniqueId(), "maggiore")));
        rolePane.addItem(new GuiItem(artificiereStack, event -> replaceRole(event.getWhoClicked().getUniqueId(), "artificiere")));
        rolePane.addItem(new GuiItem(bombaStack, event -> replaceRole(event.getWhoClicked().getUniqueId(), "bomba")));
        rolePane.addItem(new GuiItem(assassinoStack, event -> replaceRole(event.getWhoClicked().getUniqueId(), "assassino")));
        mainGui.addPane(rolePane);
    }

    private ItemStack generaleItemStack() {
        ItemStack generaleStack = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta generaleMeta = generaleStack.getItemMeta();
        assert generaleMeta != null;
        FileConfiguration config = FwHumanStratego.getDefaultConfig();
        List<String> loreStrings = new ArrayList<>();
        loreStrings.add("Puoi utilizzare questo ruolo ancora " + squad.getRolesRemaining().get(game.getRoleByName("Generale")).toString() + " volte");
        loreStrings.add("Ci possono essere altri " + (config.getInt("roles." + "Generale" + ".max_players") - game.getPlayerWhoHaveThisRole(game.getRoleByName("Generale"), squad) + " generali in gioco"));
        generaleMeta.setLore(loreStrings);
        generaleMeta.setDisplayName("Generale");
        generaleStack.setItemMeta(generaleMeta);
        return generaleStack;
    }

    private ItemStack marescialloItemStack() {
        ItemStack marescialloStack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta marescialloMeta = marescialloStack.getItemMeta();
        assert marescialloMeta != null;
        FileConfiguration config = FwHumanStratego.getDefaultConfig();
        List<String> loreStrings = new ArrayList<>();
        loreStrings.add("Puoi utilizzare questo ruolo ancora " + squad.getRolesRemaining().get(game.getRoleByName("Maresciallo")).toString() + " volte");
        loreStrings.add("Ci possono essere altri " + (config.getInt("roles." + "Maresciallo" + ".max_players") - game.getPlayerWhoHaveThisRole(game.getRoleByName("Maresciallo"), squad) + " marescialli in gioco"));
        marescialloMeta.setLore(loreStrings);
        marescialloMeta.setDisplayName("Maresciallo");
        marescialloStack.setItemMeta(marescialloMeta);
        return marescialloStack;
    }

    private ItemStack colonnelloItemStack() {
        ItemStack colonnelloStack = new ItemStack(Material.IRON_SWORD);
        ItemMeta colonnelloMeta = colonnelloStack.getItemMeta();
        assert colonnelloMeta != null;
        FileConfiguration config = FwHumanStratego.getDefaultConfig();
        List<String> loreStrings = new ArrayList<>();
        loreStrings.add("Puoi utilizzare questo ruolo ancora " + squad.getRolesRemaining().get(game.getRoleByName("Colonnello")).toString() + " volte");
        loreStrings.add("Ci possono essere altri " + (config.getInt("roles." + "Colonnello" + ".max_players") - game.getPlayerWhoHaveThisRole(game.getRoleByName("Colonnello"), squad) + " colonnelli in gioco"));
        colonnelloMeta.setLore(loreStrings);
        colonnelloMeta.setDisplayName("Colonnello");
        colonnelloStack.setItemMeta(colonnelloMeta);

        return colonnelloStack;
    }

    private ItemStack maggioreItemStack() {
        ItemStack maggioreStack = new ItemStack(Material.STONE_SWORD);
        ItemMeta maggioreMeta = maggioreStack.getItemMeta();
        assert maggioreMeta != null;
        FileConfiguration config = FwHumanStratego.getDefaultConfig();
        List<String> loreStrings = new ArrayList<>();
        loreStrings.add("Puoi utilizzare questo ruolo ancora " + squad.getRolesRemaining().get(game.getRoleByName("Maggiore")).toString() + " volte");
        loreStrings.add("Ci possono essere altri " + (config.getInt("roles." + "Maggiore" + ".max_players") - game.getPlayerWhoHaveThisRole(game.getRoleByName("Maggiore"), squad) + " maggiori in gioco"));
        maggioreMeta.setLore(loreStrings);
        maggioreMeta.setDisplayName("Maggiore");
        maggioreStack.setItemMeta(maggioreMeta);

        return maggioreStack;
    }

    private ItemStack artificiereItemStack() {
        ItemStack artificiereStack = new ItemStack(Material.FLINT_AND_STEEL);
        ItemMeta artificiereMeta = artificiereStack.getItemMeta();
        assert artificiereMeta != null;
        FileConfiguration config = FwHumanStratego.getDefaultConfig();
        List<String> loreStrings = new ArrayList<>();
        loreStrings.add("Puoi utilizzare questo ruolo ancora " + squad.getRolesRemaining().get(game.getRoleByName("Artificiere")).toString() + " volte");
        loreStrings.add("Ci possono essere altri " + (config.getInt("roles." + "Artificiere" + ".max_players") - game.getPlayerWhoHaveThisRole(game.getRoleByName("Artificiere"), squad) + " artificieri in gioco"));
        artificiereMeta.setLore(loreStrings);
        artificiereMeta.setDisplayName("Artificiere");
        artificiereStack.setItemMeta(artificiereMeta);

        return artificiereStack;
    }

    private ItemStack bombaItemStack() {
        ItemStack bombaStack = new ItemStack(Material.TNT);
        ItemMeta bombaMeta = bombaStack.getItemMeta();
        assert bombaMeta != null;
        FileConfiguration config = FwHumanStratego.getDefaultConfig();
        List<String> loreStrings = new ArrayList<>();
        loreStrings.add("Puoi utilizzare questo ruolo ancora " + squad.getRolesRemaining().get(game.getRoleByName("Bomba")).toString() + " volte");
        loreStrings.add("Ci possono essere altri " + (config.getInt("roles." + "Bomba" + ".max_players") - game.getPlayerWhoHaveThisRole(game.getRoleByName("Bomba"), squad) + " bombe in gioco"));
        bombaMeta.setLore(loreStrings);
        bombaMeta.setDisplayName("Bomba");
        bombaStack.setItemMeta(bombaMeta);

        return bombaStack;
    }

    private ItemStack assassinoItemStack() {
        ItemStack assassinoStack = new ItemStack(Material.LEAD);
        ItemMeta assassinoMeta = assassinoStack.getItemMeta();
        assert assassinoMeta != null;
        FileConfiguration config = FwHumanStratego.getDefaultConfig();
        List<String> loreStrings = new ArrayList<>();
        loreStrings.add("Puoi utilizzare questo ruolo ancora " + squad.getRolesRemaining().get(game.getRoleByName("Assassino")).toString() + " volte");
        loreStrings.add("Ci possono essere altri " + (config.getInt("roles." + "Assassino" + ".max_players") - game.getPlayerWhoHaveThisRole(game.getRoleByName("Assassino"), squad) + " assassini in gioco"));
        assassinoMeta.setLore(loreStrings);
        assassinoMeta.setDisplayName("Assassino");
        assassinoStack.setItemMeta(assassinoMeta);

        return assassinoStack;
    }

    public void updateGui() {
        addItemStack();
        mainGui.update();
    }

    public void show(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        assert player != null;
        mainGui.show(player);
    }

    public Inventory getInventory() {
        return mainGui.getInventory();
    }

    public void replaceRole(UUID uuid, String roleName) {
        Role role = game.getRoleByName(roleName);
        Player player = Bukkit.getPlayer(uuid);
        assert player != null;
        if (game.isAbleToUse(role, squad)) {
            if (game.isRemainingARole(role, squad)) {
                game.addPlayerRole(uuid, role, squad);
                player.sendTitle("il tuo nuovo ruolo e'", role.getName(), 30, 100, 30);
                squad.getRolesRemaining().put(role, squad.getRolesRemaining().get(role) - 1);
                updateGui();
                game.getScoreboard().updatePlayerRole(uuid);
                clearPlayer(uuid);
                player.closeInventory();
                if (game.isSpectateMode(squad)) {
                    game.spectateMode(squad);
                }
            } else {
                Message.GAME_ROLEFULL.send(player);
            }
        } else {
            Message.GAME_ROLEBUSY.send(player);
        }
    }

    private void clearPlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        assert player != null;
        player.setFoodLevel(20);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        if (player.getGameMode() != GameMode.SURVIVAL) {
            player.setGameMode(GameMode.SURVIVAL);
        }
        if (Objects.requireNonNull(Bukkit.getPlayer(uuid)).getWalkSpeed() != 0.2F) {
            Objects.requireNonNull(Bukkit.getPlayer(uuid)).setWalkSpeed(0.2F);
        }
    }
}
