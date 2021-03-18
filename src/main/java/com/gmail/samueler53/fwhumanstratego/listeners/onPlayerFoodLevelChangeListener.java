package com.gmail.samueler53.fwhumanstratego.listeners;

import com.gmail.samueler53.fwhumanstratego.managers.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class onPlayerFoodLevelChangeListener implements Listener {

    final GameManager gameManager = GameManager.getInstance();

    @EventHandler
    public void onPlayerFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            if (gameManager.isPlayerPlaying(event.getEntity().getUniqueId()).isPresent()) {
                event.setCancelled(true);
            }
        }
    }
}
