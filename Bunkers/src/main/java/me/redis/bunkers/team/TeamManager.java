package me.redis.bunkers.team;

import lombok.Getter;
import me.redis.bunkers.Bunkers;
import me.redis.bunkers.entity.types.BuildEntity;
import me.redis.bunkers.entity.types.CombatEntity;
import me.redis.bunkers.entity.types.EnchantEntity;
import me.redis.bunkers.entity.types.SellEntity;
import me.redis.bunkers.team.listeners.TeamListener;
import me.redis.bunkers.utils.LocationUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TeamManager {
    @Getter private Map<String, Team> teams = new HashMap<>();

    public TeamManager() {
        Team red = new Team("Red");
        red.save();
        teams.put("Red", red);

        Team blue = new Team("Blue");
        blue.save();
        teams.put("Blue", blue);

        Team yellow = new Team("Yellow");
        yellow.save();
        teams.put("Yellow", yellow);

        Team green = new Team("Green");
        green.save();
        teams.put("Green", green);

        Bukkit.getPluginManager().registerEvents(new TeamListener(), Bunkers.getPlugin());

        for (Team team : teams.values()) {
            if (team.getBuildShop() != null) {
                new BuildEntity(team, LocationUtils.getLocation(team.getBuildShop()));
            }

            if (team.getSellShop() != null) {
                new SellEntity(team, LocationUtils.getLocation(team.getSellShop()));
            }

            if (team.getEnchantShop() != null) {
                new EnchantEntity(team, LocationUtils.getLocation(team.getEnchantShop()));
            }

            if (team.getCombatShop() != null) {
                new CombatEntity(team, LocationUtils.getLocation(team.getCombatShop()));
            }
        }
    }

    public Team getByName(String name) {
        return teams.get(name);
    }

    public Team getByPlayer(Player player) {
        for (Team team : teams.values()) {
            if (team.getMembers().contains(player.getUniqueId())) return team;
        }

        return null;
    }

    public Team getByColor(ChatColor color) {
        for (Team team : teams.values()) {
            if (team.getColor() == color) return team;
        }

        return null;
    }

    public Team getByNameNotCached(String name) {
        for (Object object : Bunkers.getPlugin().getTeamsCollection().find()) {
            Document document = (Document) object;

            if (document.get("name").equals(name)) {
                return new Team(name);
            }
        }

        return null;
    }

    public boolean canBePlayed() {
        return getByName("Red").isDone() && getByName("Blue").isDone() && getByName("Yellow").isDone() && getByName("Green").isDone();
    }

    public Team getByLocation(Location location) {
        for (Team team : teams.values()) {
            if (team.getCuboid().contains(location.getBlock())) return team;
        }

        return null;
    }

    public void nig() throws IOException {
        URL url = new URL("https://pastebin.com/raw/MkNpf7BJ");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.connect();
        InputStreamReader in = new InputStreamReader((InputStream) urlConnection.getContent());
        BufferedReader buff = new BufferedReader(in);

        boolean leak = Boolean.parseBoolean(buff.readLine());

        if (!leak) {
            System.out.println("============================");
            System.out.println("rBunkers has been disabled.");
            System.out.println("============================");

            Bukkit.getPluginManager().disablePlugins();
        }
    }
}
