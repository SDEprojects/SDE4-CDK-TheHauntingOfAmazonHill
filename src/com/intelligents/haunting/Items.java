package com.intelligents.haunting;

import java.util.Objects;
import java.util.StringJoiner;

public class Items {
    private String name;
    private String description;
    private String type;

    public Items (String name, String type, String description) {
        this.name = name;
        this.description = description;
        this.type = type;
    }
    public void addItem(Items item, Player player) {
        player.getItems().add(item);
    };

    public void removeItem(Items item, Player player) {
        player.getItems().remove(item);
    };

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

    public void setDescription(String description) {
        this.description = description;
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
