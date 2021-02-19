package com.intelligents.haunting;

import java.util.Objects;
import java.util.StringJoiner;

public class Items implements java.io.Serializable {
    private String name;
    private final String description;
    private final String type;

    public Items (String name, String type, String description) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public int getDamage() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Items items = (Items) o;
        return Objects.equals(getName(), items.getName()) && Objects.equals(getDescription(), items.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription());
    }

    @Override
    public String toString() {
        return new StringJoiner("\n", "\n", "\n")
                .add("Name: '" + name + "'")
                .add("Type: " + type)
                .add("Description: " + description)
                .toString();
    }

}
