package me.redis.practice.listeners;

import me.redis.practice.Practice;
import me.redis.practice.queue.QueueListener;
import me.redis.practice.team.listeners.TeamListener;
import org.bukkit.Bukkit;

public class ListenerRegisterer {
    public ListenerRegisterer() {
        Bukkit.getPluginManager().registerEvents(new CommandListener(), Practice.getPlugin());
        Bukkit.getPluginManager().registerEvents(new WorldListener(), Practice.getPlugin());
        Bukkit.getPluginManager().registerEvents(new PlayerListeners(), Practice.getPlugin());
        Bukkit.getPluginManager().registerEvents(new MenuListener(), Practice.getPlugin());
        Bukkit.getPluginManager().registerEvents(new MatchListener(), Practice.getPlugin());
        Bukkit.getPluginManager().registerEvents(new QueueListener(), Practice.getPlugin());
        Bukkit.getPluginManager().registerEvents(new TeamListener(), Practice.getPlugin());
    }
}
