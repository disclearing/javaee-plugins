package me.javaee.uhc.menu.menu;

import me.javaee.uhc.UHC;
import me.javaee.uhc.menu.type.ChestMenu;
import me.javaee.uhc.database.profile.ProfileUtils;
import me.javaee.uhc.utils.ItemBuilder;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class AlivePlayersMenu extends ChestMenu<UHC> {
    Player player;

    public AlivePlayersMenu(Player player) {
        super(6 * 9);
        this.player = player;

        update();
    }

    public void update() {
        inventory.clear();

        for (UUID alive : UHC.getInstance().getGameManager().getAlivePlayers()) {
            if (Bukkit.getPlayer(alive) != null) {
                ItemBuilder builder = new ItemBuilder(Material.PAPER);
                builder.setName(ChatColor.GREEN + Bukkit.getPlayer(alive).getName());
                builder.addLore("");

                builder.addLore(ChatColor.YELLOW + "Kills: " + ChatColor.WHITE + ProfileUtils.getInstance().getProfile(Bukkit.getPlayer(alive).getUniqueId()).getMatchKills());
                builder.addLore(ChatColor.YELLOW + "World: " + ChatColor.WHITE + WordUtils.capitalizeFully(String.valueOf(Bukkit.getPlayer(alive).getWorld().getEnvironment())));
                builder.addLore("");
                builder.addLore(ChatColor.GREEN + "» Click to spectate «");

                inventory.addItem(builder.build());
            }
        }
    }

    @Override
    public String getTitle() {
        return "Alive Participants";
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
                ItemStack item = event.getCurrentItem();
                player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1F, 1F);

                if (item.getItemMeta() != null) {
                    player.teleport(Bukkit.getPlayer(ChatColor.stripColor(item.getItemMeta().getDisplayName())));
                    player.sendMessage(ChatColor.translateAlternateColorCodes("You have been teleported to &f" + item.getItemMeta().getDisplayName() + "&6."));
                    player.closeInventory();
                }
            }
        } else if (!topInventory.equals(clickedInventory) && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
    }
}