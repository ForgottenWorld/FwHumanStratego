package com.gmail.samueler53.fwhumanstratego.listeners;

import com.gmail.samueler53.fwhumanstratego.gui.RoleGui;
import com.gmail.samueler53.fwhumanstratego.gui.TeamGui;
import com.gmail.samueler53.fwhumanstratego.managers.GameManager;
import com.gmail.samueler53.fwhumanstratego.objects.Game;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.Objects;
import java.util.UUID;

public class onPlayerOpeningInventoriesListener implements Listener {

    GameManager gameManager = GameManager.getInstance();

    @EventHandler
    public void onPlayerOpeningInventories(InventoryOpenEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (gameManager.isPlayerPlaying(uuid).isPresent()) {
            Game game = gameManager.getGameWherePlayerPlaying(uuid);
            RoleGui roleGui = game.getSquadFromPlayer(uuid).getRoleGui();
            TeamGui teamGui = game.getTeamGui();
            Location locationInventoryOpened = event.getInventory().getLocation();
            if(!Objects.equals(locationInventoryOpened,roleGui.getInventory().getLocation()) && !Objects.equals(locationInventoryOpened,teamGui.getInventory().getLocation()) && !Objects.equals(locationInventoryOpened,game.getArena().getTreasureRedLocation()) && !Objects.equals(locationInventoryOpened,game.getArena().getTreasureBlueLocation())
            ){
                event.setCancelled(true);
            }
        }
    }
}
