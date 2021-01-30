package com.gmail.samueler53.fwhumanstratego.objects;

import com.gmail.samueler53.fwhumanstratego.gui.RoleGui;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Squad {

    private final String name;
    private int points = 0;
    private final Map<UUID, Role> playersRoles = new HashMap<>();
    private Map<Role, Integer> rolesRemaining = new HashMap<>();
    private final ItemStack kit = new ItemStack(Material.LEATHER_CHESTPLATE);
    private final RoleGui roleGui;
    private final Material treasure;

    public Squad(String name, Color color, Material treasure, Game game) {
        this.name = name;
        setKit(color);
        this.treasure = treasure;
        this.roleGui = new RoleGui(this, game);
    }

    public void addPlayer(UUID uuid) {
        playersRoles.put(uuid, null);
    }

    public void addPlayerRole(UUID uuid, Role role) {
        playersRoles.put(uuid, role);
    }

    public void removePlayer(UUID uuid) {
        playersRoles.remove(uuid);
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void clearRoles() {
        playersRoles.replaceAll((k, v) -> null);
    }

    public void setRolesRemaining(Map<Role, Integer> rolesRemaining) {
        this.rolesRemaining.putAll(rolesRemaining);
    }

    public void setKit(Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta) kit.getItemMeta();
        assert meta != null;
        meta.setColor(color);
        kit.setItemMeta(meta);
    }

    public void equipKitForEachPlayer() {
        for (UUID uuid : playersRoles.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            assert player != null;
            player.getInventory().setChestplate(kit);
        }
    }

    public Map<UUID, Role> getPlayersRoles() {
        return playersRoles;
    }

    public Map<Role, Integer> getRolesRemaining() {
        return rolesRemaining;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public Material getTreasure() {
        return treasure;
    }

    public RoleGui getRoleGui() {
        return roleGui;
    }
}
