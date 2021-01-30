package com.gmail.samueler53.fwhumanstratego.objects;

import com.gmail.samueler53.fwhumanstratego.utils.LocationSerializable;
import org.bukkit.Location;

import java.io.Serializable;
import java.util.Objects;

public class Arena implements Serializable {
    private static transient final long serialVersionUID = 1681012206529286330L;
    private final String name;
    private LocationSerializable redTeamLocation;
    private LocationSerializable blueTeamLocation;
    private LocationSerializable treasureRedLocation;
    private LocationSerializable treasureBlueLocation;
    private LocationSerializable lobbyLocation;

    public Arena(String name) {
        this.name = name;
    }

    public Location getRedTeamLocation() {
        if (redTeamLocation != null) {
            return new Location(redTeamLocation.getWorld(), redTeamLocation.getX(), redTeamLocation.getY(), redTeamLocation.getZ());
        }
        return null;
    }

    public void setRedTeamLocation(Location redTeamLocation) {
        this.redTeamLocation = new LocationSerializable(Objects.requireNonNull(redTeamLocation.getWorld()), redTeamLocation.getBlockX(), redTeamLocation.getBlockY(), redTeamLocation.getBlockZ());
    }

    public Location getBlueTeamLocation() {
        if (blueTeamLocation != null) {
            return new Location(blueTeamLocation.getWorld(), blueTeamLocation.getX(), blueTeamLocation.getY(), blueTeamLocation.getZ());
        }
        return null;
    }

    public void setBlueTeamLocation(Location blueTeamLocation) {
        this.blueTeamLocation = new LocationSerializable(Objects.requireNonNull(blueTeamLocation.getWorld()), blueTeamLocation.getBlockX(), blueTeamLocation.getBlockY(), blueTeamLocation.getBlockZ());
    }

    public Location getTreasureRedLocation() {
        if (treasureRedLocation != null) {
            return new Location(treasureRedLocation.getWorld(), treasureRedLocation.getX(), treasureRedLocation.getY(), treasureRedLocation.getZ());
        }
        return null;
    }

    public void setTreasureRedLocation(Location treasureRedLocation) {
        this.treasureRedLocation = new LocationSerializable(Objects.requireNonNull(treasureRedLocation.getWorld()), treasureRedLocation.getBlockX(), treasureRedLocation.getBlockY(), treasureRedLocation.getBlockZ());
    }

    public Location getTreasureBlueLocation() {
        if (treasureBlueLocation != null) {
            return new Location(treasureBlueLocation.getWorld(), treasureBlueLocation.getX(), treasureBlueLocation.getY(), treasureBlueLocation.getZ());
        }
        return null;
    }

    public void setTreasureBlueLocation(Location treasureBlueLocation) {
        this.treasureBlueLocation = new LocationSerializable(Objects.requireNonNull(treasureBlueLocation.getWorld()), treasureBlueLocation.getBlockX(), treasureBlueLocation.getBlockY(), treasureBlueLocation.getBlockZ());
    }

    public Location getLobbyLocation() {
        if (lobbyLocation != null) {
            return new Location(lobbyLocation.getWorld(), lobbyLocation.getX(), lobbyLocation.getY(), lobbyLocation.getZ());
        }
        return null;

    }

    public void setLobbyLocation(Location lobbyLocation) {
        this.lobbyLocation = new LocationSerializable(Objects.requireNonNull(lobbyLocation.getWorld()), lobbyLocation.getBlockX(), lobbyLocation.getBlockY(), lobbyLocation.getBlockZ());
    }

    public String getName() {
        return name;
    }
}
