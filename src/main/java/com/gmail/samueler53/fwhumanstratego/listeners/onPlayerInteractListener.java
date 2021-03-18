package com.gmail.samueler53.fwhumanstratego.listeners;

import com.gmail.samueler53.fwhumanstratego.managers.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.UUID;

public class onPlayerInteractListener implements Listener {

    final GameManager gameManager = GameManager.getInstance();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (gameManager.isPlayerPlaying(uuid).isPresent()) {
            event.setCancelled(true);
        }
    }
}
