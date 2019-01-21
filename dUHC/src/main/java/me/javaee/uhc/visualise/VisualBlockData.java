package me.javaee.uhc.visualise;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class VisualBlockData extends MaterialData {

    public VisualBlockData(Material type) {
        super(type);
    }

    public VisualBlockData(Material type, byte data) {
        super(type, data);
    }

    public Material getBlockType() {
        return getItemType();
    }

    @Override
    @Deprecated
    public Material getItemType() {
        return super.getItemType();
    }

    @Override
    @Deprecated
    public ItemStack toItemStack() {
        throw new UnsupportedOperationException("This is a VisualBlock data");
    }

    @Override
    @Deprecated
    public ItemStack toItemStack(int amount) {
        throw new UnsupportedOperationException("This is a VisualBlock data");
    }
}