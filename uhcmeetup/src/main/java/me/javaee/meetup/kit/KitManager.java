package me.javaee.meetup.kit;

import lombok.Getter;
import me.javaee.meetup.utils.FileConfig;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class KitManager {
    private FileConfig config;
    private @Getter List<Kit> kits;

    public KitManager() {
        this.config = new FileConfig("kits.yml");
        this.kits = new ArrayList<>();

        loadKits();
    }

    private void loadKits() {
        if (config.getConfig().contains("kits")) {
            for (int i = 0; i < 30; i++) {
                if (config.getConfig().get("kits." + i) == null) {
                    continue;
                }

                List<ItemStack> inventoryList = (List<ItemStack>) config.getConfig().get("kits." + i + ".inventory");
                ItemStack[] inventory = inventoryList.toArray(new ItemStack[0]);
                List<ItemStack> armorList = (List<ItemStack>) config.getConfig().get("kits." + i + ".armor");
                ItemStack[] armor = armorList.toArray(new ItemStack[0]);

                Kit kit = new Kit(i, inventory, armor);

                kits.add(kit);
            }
        }
    }

    public void createKit(Kit kit) {
        kits.add(kit);

        config.getConfig().set("kits." + kit.getNumber() + ".inventory", kit.getInventory());
        config.getConfig().set("kits." + kit.getNumber() + ".armor", kit.getArmor());
        config.save();
    }
}
