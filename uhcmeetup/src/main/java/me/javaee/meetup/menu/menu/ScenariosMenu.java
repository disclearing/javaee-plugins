package me.javaee.meetup.menu.menu;

import me.javaee.meetup.Meetup;
import me.javaee.meetup.enums.GameState;
import me.javaee.meetup.handlers.Scenario;
import me.javaee.meetup.menu.type.ChestMenu;
import me.javaee.meetup.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.ChatPaginator;

public class ScenariosMenu extends ChestMenu<Meetup> {
    private Player player;

    public ScenariosMenu(Player player) {
        super(18);
        this.player = player;

        update();
        new BukkitRunnable() {
            public void run() {
                if (Meetup.getPlugin().getGameManager().getGameState() == GameState.WAITING) {
                    update();
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(Meetup.getPlugin(), 20L, 20L);
    }

    public void update() {
        inventory.clear();
        for (Scenario scenarios : plugin.getScenarios()) {
            ItemBuilder builder = new ItemBuilder(scenarios.getItemStack().clone());
            builder.setName(ChatColor.GREEN + scenarios.getName());

            if (scenarios.getDescription() != null) {
                if (scenarios.getVotes() >= 5) {
                    builder.addLore(ChatColor.translateAlternateColorCodes('&', "&6Votes&7:&f " + "5/5"));
                } else {
                    builder.addLore(ChatColor.translateAlternateColorCodes('&', "&6Votes&7:&f " + scenarios.getVotes() + "/5"));
                }
                builder.addLore("");

                String[] paginator = ChatPaginator.wordWrap(ChatColor.GOLD + scenarios.getDescription(), 40);

                for (String string : paginator) {
                    string = ChatColor.GOLD + string;

                    builder.addLore(ChatColor.GOLD + string);
                }
            }
            inventory.addItem(builder.build());
        }
    }

    @Override
    public String getTitle() {
        return "Vote Menu";
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
                if (event.getCurrentItem().getItemMeta() != null) {
                    if (Meetup.getPlugin().getVoted().contains(player)) {
                        player.sendMessage(ChatColor.RED + "You have already voted.");
                        return;
                    }

                    if (player.hasPermission("vip")) {
                        Scenario.getByName(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())).setVotes(Scenario.getByName(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())).getVotes() + 3);
                    } else {
                        Scenario.getByName(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())).setVotes(Scenario.getByName(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())).getVotes() + 1);
                    }
                    Meetup.getPlugin().getVoted().add(player);
                    player.closeInventory();
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