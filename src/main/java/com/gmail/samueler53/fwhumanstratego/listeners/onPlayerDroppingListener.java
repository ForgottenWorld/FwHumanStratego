package com.gmail.samueler53.fwhumanstratego.listeners;

import com.gmail.samueler53.fwhumanstratego.managers.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class onPlayerDroppingListener implements Listener {

    GameManager gameManager = GameManager.getInstance();

    @EventHandler
    public void onPlayerDropping(PlayerDropItemEvent event){
        if(gameManager.isPlayerPlaying(event.getPlayer().getUniqueId()).isPresent()){
            event.setCancelled(true);
        }
    }
}
