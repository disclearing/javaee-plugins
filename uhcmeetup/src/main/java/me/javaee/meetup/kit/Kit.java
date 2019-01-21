package me.javaee.meetup.kit;

import lombok.Getter;
import me.javaee.meetup.Meetup;
import org.bukkit.inventory.ItemStack;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
@Getter
public class Kit {
    private Integer number;
    private ItemStack[] inventory;
    private ItemStack[] armor;

    public Kit(Integer number, ItemStack[] inventory, ItemStack[] armor) {
        this.number = number;
        this.inventory = inventory;
        this.armor = armor;

        Meetup.getPlugin().getConfig().set(number + ".armor", armor);
        Meetup.getPlugin().getConfig().set(number + ".inventory", inventory);
    }
}
