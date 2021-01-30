package com.gmail.samueler53.fwhumanstratego.objects;

public class Role {

    String name;
    int points;
    String description;

    public Role(String name, int points, String description) {
        this.name = name;
        this.points = points;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public String getDescription() {
        return description;
    }
}