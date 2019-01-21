package me.redis.practice.ladders.commands.arguments;

import me.redis.practice.Practice;
import me.redis.practice.ladders.Ladder;
import me.redis.practice.utils.ItemBuilder;
import me.redis.practice.utils.SerializationUtils;
import me.redis.practice.utils.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ListLadderArgument extends CommandArgument implements Listener {
    public ListLadderArgument() {
        super("list");

        Bukkit.getPluginManager().registerEvents(this, Practice.getPlugin());
    }

    @Override
    public String getUsage(String label) {
        return ChatColor.RED + "/" + label + ' ' + getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Inventory inventory = Bukkit.createInventory(null, 9, "Ladders");

            for (Ladder ladder : Practice.getPlugin().getLadderManager().getLadders().values()) {
                ItemStack item = SerializationUtils.itemStackFromString(ladder.getIcon());

                inventory.addItem(new ItemBuilder(item).setDisplayName("&a" + ladder.getName()).create());
            }

            ((Player) sender).openInventory(inventory);
        }

        return true;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getCurrentItem() != null) {
            if (event.getClickedInventory().getName() != null && event.getClickedInventory().getName().equalsIgnoreCase("Ladders")) {
                if (event.getCurrentItem().getItemMeta() != null && event.getCurrentItem().getItemMeta().getDisplayName() != null) {
                    Ladder ladder = Practice.getPlugin().getLadderManager().getLadder(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));

                    if (ladder != null) {
                        SerializationUtils.playerInventoryFromString(ladder.getDefaultInventory(), (Player) event.getWhoClicked());
                        event.getWhoClicked().closeInventory();
                    }

                    event.setCancelled(true);
                }
            }
        }
    }
}
