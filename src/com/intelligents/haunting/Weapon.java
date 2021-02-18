package com.intelligents.haunting;

import java.util.StringJoiner;

public class Weapon extends Items{
    private int damagePoints;

    Weapon(String name, String description, int damagePoints) {
        super(name, "weapon", description);
        this.damagePoints = damagePoints;
    }

    public int getDamage() {
        return this.damagePoints;
    }

    @Override
    public String toString() {
        return new StringJoiner("\n", "\n", "\n")
                .add("Name: '" + super.getName() + "'")
                .add("Type: " + super.getType())
                .add("Description: " + super.getDescription())
                .add("Damage: " + damagePoints)
                .toString();
    }

}
