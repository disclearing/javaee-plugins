package me.javaee.meetup.managers;

import lombok.Getter;
import lombok.Setter;
import me.javaee.meetup.Meetup;
import me.javaee.meetup.enums.GameState;
import me.javaee.meetup.events.BorderShrinkSetEvent;
import me.javaee.meetup.events.GameStartEvent;
import me.javaee.meetup.tasks.GameTimeTask;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutExperience;
import net.silexpvp.nightmare.util.ItemCreator;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
@Getter public class GameManager {
    @Setter private GameState gameState;
    private boolean hasCountdownStart;
    private Integer countDownTask;
    @Setter private Integer countdown = 30;
    private List<Player> alivePlayers = new ArrayList<>();
    private int joinedPlayers;
    @Setter private int currentRadius2 = 2000;
    @Setter private boolean canJoin = false;
    @Setter private Player winner;
    private ArrayList<Player> moderators = new ArrayList<>();

    public void startCountdown() {
        this.hasCountdownStart = true;
        countDownTask = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Meetup.getPlugin(), new Runnable() {
            @Override
            public void run() {
                countdown -= 1;

                if (countdown <= 30) {
                    setGameState(GameState.STARTING);

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
                        PacketPlayOutExperience packet = new PacketPlayOutExperience(0, 0, countdown);

                        entityPlayer.playerConnection.sendPacket(packet);
                    }
                }

                if (countdown <= 5 && countdown > 0) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6&lUHCMeetup&7] &6The game will begin in &f" + countdown + " seconds&6."));

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 100000, 100000);
                    }
                }

                if (countdown == 25) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6&lUHCMeetup&7] &6The game will begin in &f" + countdown + " seconds&6."));
                } else if (countdown == 20) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6&lUHCMeetup&7] &6The game will begin in &f" + countdown + " seconds&6."));
                } else if (countdown == 15) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6&lUHCMeetup&7] &6The game will begin in &f" + countdown + " seconds&6."));
                } else if (countdown == 10) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6&lUHCMeetup&7] &6The game will begin in &f" + countdown + " seconds&6."));
                } else if (countdown == 0) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6&lUHCMeetup&7] &6&lThis is a solo game, any form of teaming will result in a &c&lban&6&l."));

                    setGameState(GameState.INGAME);

                    new GameTimeTask().runTaskTimer(Meetup.getPlugin(), 20L, 20L);
                    alivePlayers.addAll(Bukkit.getOnlinePlayers());
                    joinedPlayers = Bukkit.getOnlinePlayers().size();

                    if (Meetup.getPlugin().getBorderShrink() != null && Meetup.getPlugin().getBorderShrink()) {
                        BorderShrinkSetEvent event = new BorderShrinkSetEvent();
                        Meetup.getPlugin().getServer().getPluginManager().callEvent(event);

                        int startTime = Meetup.getPlugin().getBorderShrinkTask().startTime;
                        Meetup.getPlugin().getBorderShrinkTask().runTaskTimer(Meetup.getPlugin(), startTime * 1200L, 20L);
                    }

                    Meetup.getPlugin().setStarted(true);

                    GameStartEvent gameStartEvent = new GameStartEvent();
                    Bukkit.getPluginManager().callEvent(gameStartEvent);

                    cancelTask();
                }
            }
        }, 0, 20L);
    }

    public void cancelTask() {
        Bukkit.getServer().getScheduler().cancelTask(countDownTask);

        this.countDownTask = 0;
    }


    public void enableStaffMode(Player player) {
        Inventory inventory = player.getInventory();

        Bukkit.getOnlinePlayers().forEach(online -> {
            online.hidePlayer(player);
            player.showPlayer(online);

            if (online.hasPermission("litebans.tempban")) {
                online.showPlayer(player);
            }
        });

        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(true);
        player.setCanPickupItems(false);
        player.setHealth(20);
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        player.setItemOnCursor(null);

        // 8 (gris) , 10

        inventory.addItem(new ItemCreator(Material.COMPASS).setDisplayName("&6Compass").create());
        inventory.addItem(new ItemCreator(Material.BOOK).setDisplayName("&6Inventory Inspector").create());
        inventory.addItem(new ItemCreator(Material.WOOD_AXE).setDisplayName("&6Wand").create());
        inventory.addItem(new ItemCreator(Material.CARPET).setDisplayName("&6Hand hider").create());
        inventory.setItem(7, new ItemCreator(Material.SKULL_ITEM).setDurability(3).setDisplayName("&6Online Staff").create());
        inventory.setItem(8, new ItemCreator(Material.INK_SACK).setDurability(8).setDisplayName("&6You are vanished").create());

        Meetup.getPlugin().getGameManager().getModerators().add(player);
        alivePlayers.remove(player);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYour staff mode has been activated."));
    }
}
