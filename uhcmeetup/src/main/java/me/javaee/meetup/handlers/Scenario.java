package me.javaee.meetup.handlers;

import lombok.Getter;
import lombok.Setter;
import me.javaee.meetup.Meetup;
import org.bukkit.inventory.ItemStack;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */

public class Scenario {
    @Getter private String name;
    @Getter private ItemStack itemStack;
    @Getter private String description;
    @Getter @Setter boolean enabled;
    @Getter @Setter int votes;

    public Scenario(String name, ItemStack itemStack, String description) {
        this.name = name;
        this.itemStack = itemStack;
        this.description = description;

        votes = 0;
        enabled = false;
        Meetup.getPlugin().getScenarios().add(this);
    }

    public static Scenario getByName(String name) {
        for (Scenario scenario : Meetup.getPlugin().getScenarios()) {
            if (scenario.getName().equalsIgnoreCase(name)) {
                return scenario;
            }
        }
        return null;
    }
}
