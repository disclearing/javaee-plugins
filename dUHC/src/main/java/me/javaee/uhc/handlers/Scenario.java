package me.javaee.uhc.handlers;

import lombok.Getter;
import lombok.Setter;
import me.javaee.uhc.UHC;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */

public class Scenario {
    @Getter private String name;
    @Getter private ItemStack itemStack;
    @Getter private String description;
    @Getter private String prefix;
    @Getter @Setter boolean enabled;

    public Scenario(String name, String prefix, ItemStack itemStack, String description) {
        this.name = name;
        this.prefix = prefix;
        this.itemStack = itemStack;
        this.description = description;
        enabled = false;
    }

    public static Scenario getByName(String name) {
        for (Scenario scenario : UHC.getInstance().getScenarios()) {
            if (scenario.getName().equalsIgnoreCase(name)) {
                return scenario;
            }
        }
        return null;
    }
}
