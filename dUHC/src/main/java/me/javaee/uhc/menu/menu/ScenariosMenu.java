package me.javaee.uhc.menu.menu;

import me.javaee.uhc.UHC;
import me.javaee.uhc.events.ScenarioDisableEvent;
import me.javaee.uhc.events.ScenarioEnableEvent;
import me.javaee.uhc.handlers.Scenario;
import me.javaee.uhc.menu.type.ChestMenu;
import me.javaee.uhc.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.ChatPaginator;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ScenariosMenu extends ChestMenu<UHC> {
    private Player player;

    public ScenariosMenu(Player player) {
        super(36);
        this.player = player;

        update();
    }

    public void update() {
        inventory.clear();
        for (Scenario scenarios : plugin.getScenarios()) {
            ItemBuilder builder = new ItemBuilder(scenarios.getItemStack().clone());
            builder.setName(scenarios.isEnabled() ? ChatColor.GREEN + scenarios.getName() : ChatColor.RED + scenarios.getName());

            if (player == UHC.getInstance().getGameManager().getHost()) {
                if (scenarios.isEnabled()) {
                    if (scenarios.getDescription() != null) {
                        builder.addLore("");

                        builder.addLore(ChatColor.YELLOW + "Active: " + ChatColor.GREEN + "Yes");
                        builder.addLore(ChatColor.YELLOW + "Click to toggle");
                        builder.addLore("");

                        String[] paginator = ChatPaginator.wordWrap(ChatColor.BLUE + scenarios.getDescription(), 49);

                        for (String string : paginator) {
                            string = ChatColor.BLUE + string;

                            builder.addLore(ChatColor.BLUE + string);
                        }
                    }
                } else {
                    if (scenarios.getDescription() != null) {
                        builder.addLore("");

                        builder.addLore(ChatColor.YELLOW + "Active: " + ChatColor.RED + "No");
                        builder.addLore(ChatColor.YELLOW + "Click to toggle");

                        builder.addLore("");

                        String[] paginator = ChatPaginator.wordWrap(ChatColor.BLUE + scenarios.getDescription(), 49);

                        for (String string : paginator) {
                            string = ChatColor.BLUE + string;

                            builder.addLore(ChatColor.BLUE + string);
                        }
                    }
                }
                inventory.addItem(builder.build());
            } else {
                if (scenarios.isEnabled()) {
                    if (scenarios.getDescription() != null) {
                        builder.addLore("");

                        String[] paginator = ChatPaginator.wordWrap(ChatColor.BLUE + scenarios.getDescription(), 49);

                        for (String string : paginator) {
                            string = ChatColor.BLUE + string;

                            builder.addLore(ChatColor.BLUE + string);
                        }
                    }
                    inventory.addItem(builder.build());
                }
            }
        }
    }

    @Override
    public String getTitle() {
        return "Scenarios Menu";
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();
        if (clickedInventory == null || topInventory == null || !topInventory.equals(inventory)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (topInventory.equals(clickedInventory)) {
            event.setCancelled(true);

            if (event.getCurrentItem().getType() != Material.AIR) {
                if (event.getWhoClicked() == UHC.getInstance().getGameManager().getHost()) {
                    ItemStack item = event.getCurrentItem();
                    player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1F, 1F);

                    if (item.getItemMeta() != null) {
                        if (item.getItemMeta().getDisplayName().contains(ChatColor.GREEN + "")) {
                            Bukkit.getPluginManager().callEvent(new ScenarioDisableEvent(Scenario.getByName(ChatColor.stripColor(item.getItemMeta().getDisplayName()))));
                            Scenario.getByName(ChatColor.stripColor(item.getItemMeta().getDisplayName())).setEnabled(false);
                        } else if (item.getItemMeta().getDisplayName().contains(ChatColor.RED + "")) {
                            Bukkit.getPluginManager().callEvent(new ScenarioEnableEvent(Scenario.getByName(ChatColor.stripColor(item.getItemMeta().getDisplayName()))));
                            Scenario.getByName(ChatColor.stripColor(item.getItemMeta().getDisplayName())).setEnabled(true);
                        }
                        update();
                    }
                }
            }
        } else if (!topInventory.equals(clickedInventory) && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        if (topInventory.equals(getInventory())) {
            event.setCancelled(true);
        }
    }
}