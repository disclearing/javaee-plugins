package me.redis.practice.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/")) {
            String command = event.getMessage();

            if (command.equalsIgnoreCase("/day")) {
                event.getPlayer().setPlayerTime(6000, false);
                event.getPlayer().sendMessage(ChatColor.GREEN + "Your time is now day!");
            } else if (command.equalsIgnoreCase("/night")) {
                event.getPlayer().setPlayerTime(18000, false);
                event.getPlayer().sendMessage(ChatColor.GREEN + "Your time is now night!");
            }
        }
    }
}
