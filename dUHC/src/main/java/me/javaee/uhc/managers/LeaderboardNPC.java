package me.javaee.uhc.managers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import lombok.Getter;
import me.javaee.uhc.UHC;
import me.javaee.uhc.utils.GATextureFetcher;
import me.javaee.uhc.utils.MojangNameFetcher;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.WorldServer;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.silexpvp.nightmare.util.LuckPermsUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
public class LeaderboardNPC {
    private UHC instance = UHC.getInstance();
    private UUID uuid;
    private Location location;
    private String name;
    private String nonFormattedName;

    public LeaderboardNPC(UUID uuid, Location location) {
        this.uuid = uuid;
        this.location = location;
        this.name = LuckPermsUtils.getPrefix(uuid) + getName(uuid);
        this.nonFormattedName = getName(uuid);

        Player player = location.getWorld().spawnNPC(UUID.randomUUID(), ChatColor.GOLD + (nonFormattedName.length() > 14 ? nonFormattedName.substring(0, 14) : nonFormattedName), "", "", location);
        setSkin(((CraftPlayer) player).getProfile(), uuid);

        UHC.getInstance().getNpcs().add(player);
    }

    private String getName(UUID uuid) {
        ArrayList<UUID> uuids = new ArrayList<>();
        uuids.add(uuid);

        MojangNameFetcher fetcher = new MojangNameFetcher(uuids);
        try {
            return fetcher.call().get(uuid).getValue().getCurrentName();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static final Cache<UUID, GameProfile> properties = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(new CacheLoader<UUID, GameProfile>() {

                @Override
                public GameProfile load(UUID uuid) {
                    return MinecraftServer.getServer().av().fillProfileProperties(new GameProfile(uuid, null), true);
                }
            });

    public static void setSkin(GameProfile profile, UUID skinId) {
        GameProfile skinProfile;
        if (Bukkit.getPlayer(skinId) != null) {
            skinProfile = getHandle(Bukkit.getPlayer(skinId)).getProfile();
        } else {
            skinProfile = properties.getUnchecked(skinId);
        }
        if (skinProfile.getProperties().containsKey("textures")) {
            profile.getProperties().removeAll("textures");
            profile.getProperties().putAll("textures", skinProfile.getProperties().get("textures"));
        }
    }

    public static EntityPlayer getHandle(Player player) {
        if (!(player instanceof CraftPlayer)) throw new UnsupportedOperationException("Nono");
        return ((CraftPlayer) player).getHandle();
    }
}
