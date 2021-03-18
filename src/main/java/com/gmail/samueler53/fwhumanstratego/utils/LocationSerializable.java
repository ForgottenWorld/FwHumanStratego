package com.gmail.samueler53.fwhumanstratego.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.Serializable;
import java.util.UUID;

public class LocationSerializable implements Serializable {
    private static transient final long serialVersionUID = 1681012206529286330L;
    final UUID world;
    final int x;
    final int y;
    final int z;

    public LocationSerializable(World world, int x, int y, int z) {
        this.world = world.getUID();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public World getWorld() {
        return Bukkit.getWorld(world);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
