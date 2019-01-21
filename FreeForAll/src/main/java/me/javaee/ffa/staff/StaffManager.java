package me.javaee.ffa.staff;

import lombok.Getter;
import me.javaee.ffa.FFA;
import me.javaee.ffa.profiles.Profile;
import me.javaee.ffa.profiles.status.PlayerStatus;
import me.javaee.ffa.staff.listeners.StaffListener;
import me.javaee.ffa.utils.ItemBuilder;
import me.javaee.ffa.utils.LocationUtils;
import me.javaee.ffa.utils.SerializationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class StaffManager {
    public StaffManager() {
        Bukkit.getPluginManager().registerEvents(new StaffListener(), FFA.getPlugin());
    }

    public void enable(Player player) {
        Profile profile = FFA.getPlugin().getProfileManager().getProfile(player);
        profile.setPlayerStatus(PlayerStatus.STAFF);

        player.setSaturation(14);
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(true);
        player.setHealth(20);
        player.setFoodLevel(20);

        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getInventory().clear();

        player.getInventory().setItem(0, new ItemBuilder(Material.COMPASS).setDisplayName("&6Teleporter").create());
        player.getInventory().setItem(1, new ItemBuilder(Material.BOOK).setDisplayName("&6Inventory Inspector").create());

        if (player.hasPermission("worldedit.wand")) {
            player.getInventory().setItem(2, new ItemBuilder(Material.WOOD_AXE).setDisplayName("&6Wand").create());
        }

        player.getInventory().setItem(7, new ItemBuilder(Material.EYE_OF_ENDER).setDisplayName("&6Random Teleport").create());
        player.getInventory().setItem(8, new ItemBuilder(Material.INK_SACK).setDurability(8).setDisplayName("&6You are vanished").create());
        profile.setVanished(true);
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!online.hasPermission("litebans.tempban")) {
                online.hidePlayer(player);
            }

            player.showPlayer(online);
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYour staff mode has been activated."));
    }

    public void disable(Player player) {
        Profile profile = FFA.getPlugin().getProfileManager().getProfile(player);
        profile.setPlayerStatus(PlayerStatus.PLAYING);

        player.setSaturation(14);
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setHealth(20);
        player.setFoodLevel(20);

        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getInventory().clear();
        player.teleport(LocationUtils.getLocation(FFA.getPlugin().getInformationManager().getInformation().getLobbyLocation()));
        SerializationUtils.setKitToPlayer(player);
        //Setearle el kit que tiene guardado/default...

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.showPlayer(player);
            player.showPlayer(online);
        }
        profile.setVanished(false);

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYour staff mode has been desactivated."));
    }
}
