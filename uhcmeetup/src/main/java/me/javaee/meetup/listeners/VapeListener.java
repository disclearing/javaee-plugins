package me.javaee.meetup.listeners;

import me.javaee.meetup.Meetup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VapeListener implements Listener, PluginMessageListener {

    public static Set<UUID> vapers = new HashSet<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("§8 §8 §1 §3 §3 §7 §8 ");
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] data) {
        String str;
        try {
            str = new String(data);
        } catch (Exception ex) {
            str = "";
        }

        vapers.add(player.getUniqueId());
        Bukkit.getOnlinePlayers().forEach(online -> {
            if (online.hasPermission("litebans.tempban")) {
                online.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&m-------------------------------------"));
                online.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l" + player.getName() + " &e&lhas logged in with &c&lVape&e&l."));
                online.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&m-------------------------------------"));
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        vapers.remove(event.getPlayer().getUniqueId());
    }
}
