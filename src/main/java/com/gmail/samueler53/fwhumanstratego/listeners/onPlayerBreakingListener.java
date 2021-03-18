package com.gmail.samueler53.fwhumanstratego.listeners;

import com.gmail.samueler53.fwhumanstratego.managers.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;


public class onPlayerBreakingListener implements Listener{

    final GameManager gameManager = GameManager.getInstance();

    @EventHandler
    public void onPlayerBreaking(BlockBreakEvent event){
        if(gameManager.isPlayerPlaying(event.getPlayer().getUniqueId()).isPresent()){
            event.setCancelled(true);
        }
    }
}
