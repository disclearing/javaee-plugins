package me.javaee.uhc.team;

import lombok.Getter;
import lombok.Setter;
import me.javaee.uhc.UHC;
import me.javaee.uhc.enums.GameState;
import me.javaee.uhc.handlers.Scenario;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;

import java.util.*;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
@Getter
public class UHCTeam {
    @Setter private UUID leader;
    @Setter private int number;
    private List<UUID> playerList;
    private Set<UUID> inviteList;
    private Inventory inventory;
    @Setter private int kills;
    private int dtr;
    private String name;
    @Setter private String displayName;

    public UHCTeam(UUID leader) {
        setLeader(leader);
        setNumber(UHC.getInstance().getTeams().size() + 1);
        playerList = new ArrayList<>();
        inviteList = new HashSet<>();
        playerList.add(leader);
        kills = 0;
        dtr = 0;
        inventory = Bukkit.createInventory(null, 27, "Team #" + number);

        if (UHC.getInstance().getGameManager().getGameState() == GameState.WAITING) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&eTeam &9#" + number + " &ehas been created by &a" + Bukkit.getPlayer(leader).getName() + "&e."));
        }
    }

    public UHCTeam(String name, String displayName) {
        setNumber(UHC.getInstance().getTeams().size() + 1);
        this.name = name;
        this.displayName = displayName;
        playerList = new ArrayList<>();
        kills = 0;
        dtr = 0;
        inventory = Bukkit.createInventory(null, 27, "Team #" + number);


        if (UHC.getInstance().getGameManager().getGameState() == GameState.WAITING) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&eTeam &9" + getColor() + name + " &ehas been created."));
        }
    }

    public static UHCTeam getByUUID(UUID uuid) {
        for (UHCTeam team : UHC.getInstance().getTeams()) {
            if (team.getPlayerList().contains(uuid)) {
                return team;
            }
        }
        return null;
    }

    public static UHCTeam getByNumber(int number) {
        for (UHCTeam team : UHC.getInstance().getTeams()) {
            if (team.getNumber() == number) {
                return team;
            }
        }
        return null;
    }

    public static UHCTeam getByName(String name) {
        for (UHCTeam team : UHC.getInstance().getTeams()) {
            if (team.getName() != null && team.getName().equalsIgnoreCase(name)) {
                return team;
            }
        }

        return null;
    }

    public static UHCTeam getByDisplayName(String name) {
        for (UHCTeam team : UHC.getInstance().getTeams()) {
            if (team.getDisplayName() != null && team.getDisplayName().equalsIgnoreCase(name)) {
                return team;
            }
        }

        return null;
    }

    public void setDtr(int dtr) {
        this.dtr = dtr;
        System.out.printf("Added dtr");
    }

    public ChatColor getColor() {
        return ChatColor.valueOf(name.toUpperCase());
    }
}
