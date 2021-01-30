package com.gmail.samueler53.fwhumanstratego.listeners;

import com.gmail.samueler53.fwhumanstratego.FwHumanStratego;
import com.gmail.samueler53.fwhumanstratego.managers.GameManager;
import com.gmail.samueler53.fwhumanstratego.message.Message;
import com.gmail.samueler53.fwhumanstratego.objects.Game;
import com.gmail.samueler53.fwhumanstratego.objects.Squad;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.Objects;
import java.util.UUID;

public class onPlayerInventoryClickListener implements Listener {

    GameManager gameManager = GameManager.getInstance();

    @EventHandler

    public void OnPlayerInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            UUID uuid = player.getUniqueId();
            if (gameManager.isPlayerPlaying(uuid).isPresent()) {
                Game game = gameManager.getGameWherePlayerPlaying(uuid);
                if (isClickingOnArmor(event.getSlotType())) {
                    Message.GAME_TOGGLEARMOR.send(player);
                    event.setCancelled(true);
                }
                try {
                    if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                        event.setCancelled(true);
                    } else if (game.isStarted()) {
                        Squad squad = game.getSquadFromPlayer(uuid);
                        Material treasure = squad.getTreasure();
                        Squad otherSquad = game.getOtherSquad(squad);
                        Material otherTreasure = otherSquad.getTreasure();
                        if (event.getCurrentItem() != null && isStealingTreasure(event.getCurrentItem().getType(), treasure)) {
                            Message.GAME_STEALHISWOOL.send(player);
                            event.setCancelled(true);
                        } else if (Objects.equals(Objects.requireNonNull(event.getClickedInventory()).getLocation(), game.getArena().getTreasureBlueLocation()) && squad.getName().equalsIgnoreCase("red")) {
                            if (isStealingTreasure(event.getCurrentItem().getType(), otherTreasure)) {
                                Message.GAME_STEALEDWOOLBLUE.broadcast(game, player.getDisplayName());
                            }
                        } else if (Objects.equals(Objects.requireNonNull(event.getClickedInventory()).getLocation(), game.getArena().getTreasureRedLocation()) && squad.getName().equalsIgnoreCase("blue")) {
                            if (isStealingTreasure(event.getCurrentItem().getType(), otherTreasure)) {
                                Message.GAME_STEALEDWOOLRED.broadcast(game, player.getDisplayName());
                            }
                        } else if (event.getView().getTopInventory().getLocation() != null && event.getCurrentItem() != null && (event.getCurrentItem().getType().equals(Material.RED_WOOL) || event.getCurrentItem().getType().equals(Material.BLUE_WOOL)) && (!Objects.equals(event.getView().getTopInventory().getLocation(), game.getArena().getTreasureRedLocation()) && !Objects.equals(event.getView().getTopInventory().getLocation(), game.getArena().getTreasureBlueLocation()))) {
                            event.setCancelled(true);
                        }
                        if ((Objects.equals(event.getInventory().getLocation(), game.getArena().getTreasureRedLocation()) && squad.getName().equalsIgnoreCase("red")) || (Objects.equals(event.getInventory().getLocation(), game.getArena().getTreasureBlueLocation()) && squad.getName().equalsIgnoreCase("blue"))) {
                            Bukkit.getScheduler().runTaskLater(FwHumanStratego.getPlugin(), () -> {
                                if (event.getInventory().contains(otherTreasure)) {
                                    stoleWool(squad, game);
                                }
                            }, 1);
                        }
                    }
                } catch (Exception ignored) {

                }
            }
        }
    }

    private boolean isClickingOnArmor(InventoryType.SlotType slotType) {
        return slotType.equals(InventoryType.SlotType.ARMOR);
    }

    private boolean isStealingTreasure(Material currentItem, Material treasure) {
        return currentItem.equals(treasure);
    }

    private void stoleWool(Squad squad, Game game) {
        Message.GAME_TREASURESTEALED.broadcast(game);
        squad.addPoints(FwHumanStratego.getDefaultConfig().getInt("pointsFromTreasure"));
        game.endRound(squad);
    }
}
