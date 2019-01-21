package me.javaee.uhc.handlers;

import me.javaee.uhc.UHC;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedEntitySpawn;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.minecraft.util.com.mojang.authlib.properties.PropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class SkinChangeFactory {
    private static final String URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";
    private static final String SUCCESS = ChatColor.GREEN + "Your skin has been updated,";
    private static final String FAILURE = ChatColor.RED + "We could not update your skin.";

    private static BukkitTask updateTask;
    private static List<SkinChangeFactory> queue = new ArrayList<SkinChangeFactory>();
    private static long lastUpdate;

    private final UHC plugin;
    private final Player p;
    private final OfflinePlayer target;

    public SkinChangeFactory(UHC plugin, Player p, OfflinePlayer target) {
        this.plugin = plugin;
        this.p = p;
        this.target = target;

        queue.add(this);

        if(updateTask == null)
            updateTask = new BukkitRunnable() {
                @Override
                public void run() {
                    queue.get(0).change();
                    cancel();
                }

                @Override
                public void cancel() {
                    updateTask = null;
                    super.cancel();
                }
            }.runTaskAsynchronously(plugin/*, 20L*/);
    }
    private void change() {
        if(plugin == null || p == null || !p.isOnline() || target == null) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                UUID uuid = target.getUniqueId();
                try {
                    URL url = new URL(String.format(URL, uuid.toString().replace("-", "")));
                    URLConnection con = url.openConnection();
                    con.setUseCaches(false);
                    con.setDefaultUseCaches(false);
                    con.addRequestProperty("User-Agent", "Mozilla/5.0");
                    con.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
                    con.addRequestProperty("Pragma", "no-cache");
                    Scanner scanner = new Scanner(con.getInputStream(), "UTF-8").useDelimiter("\\A");
                    String json = scanner.next();

                    JSONArray properties = (JSONArray) ((JSONObject) new JSONParser().parse(json)).get("properties");

                    if(properties.size() != 1)
                        throw new Exception("Properties is an abnormal size of " + properties.size());
                    JSONObject jo = (JSONObject) properties.get(0);

                    String name = getFromJSONObject(jo, "name");
                    String value = getFromJSONObject(jo, "value");
                    String signature = getFromJSONObject(jo, "signature");

                    String failed = "Error fetching %s's skin. These variables were null:";
                    StringBuilder sb = new StringBuilder(failed);

                    if(name == null)
                        sb.append(" name");
                    if(value == null)
                        sb.append(" value");
                    if(signature == null)
                        sb.append(" signature");

                    if(failed.length() != sb.length())
                        throw new Exception(sb.toString());

                    changeSkin(name, value, signature);
                } catch(Exception e) {
                    e.printStackTrace();
                    done(false);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    private String getFromJSONObject(JSONObject jo, String key) {
        return jo.containsKey(key) ? (String) jo.get(key) : null;
    }

    private void changeSkin(final String name, final String value, final String signature) {
        new BukkitRunnable() {
            @Override
            public void run() {
                GameProfile profile = ((CraftPlayer) p).getProfile();
                PropertyMap map = profile.getProperties();
                List<Property> toRemove = new ArrayList<Property>();

                for(Property property : map.get(name))
                    toRemove.add(property);
                for(Property property : toRemove)
                    map.remove(name, property);
                map.put(name, new Property(name, value, signature));

                PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn(((CraftPlayer) p).getHandle());
                for(Player pp : Bukkit.getOnlinePlayers())
                    if(!pp.equals(p))
                        ((CraftPlayer) pp).getHandle().playerConnection.sendPacket(packet);
                done(true);
            }
        }.runTask(plugin);
    }

    private void done(final boolean success) {
        new BukkitRunnable() {
            @Override
            public void run() {
                p.sendMessage(success ? SUCCESS : FAILURE);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.canSee(p)) {
                        player.hidePlayer(p);
                        player.showPlayer(p);
                    }
                }

                queue.remove(SkinChangeFactory.this);
            }
        }.runTask(plugin);
    }
}