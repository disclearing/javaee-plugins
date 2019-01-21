package me.redis.practice.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PracticeUtils {
    public static Map<Player, Integer> packets = new HashMap<>();

    public static void resetPlayer(Player player) {
        player.setCanPickupItems(false);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setFireTicks(1);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    }

    public static ItemStack[] getSpectatorInventory() {
        return new ItemStack[]{
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new ItemBuilder(Material.WOODEN_DOOR).setDisplayName("&cLeave the match").create()
        };
    }

    public static ItemStack[] getQueueInventory() {
        return new ItemStack[]{
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new ItemBuilder(Material.INK_SACK).setDurability(1).setDisplayName("&cLeave queue").create()
        };
    }

    public static ItemStack[] getLobbyInventory() {
        return new ItemStack[]{
                new ItemBuilder(Material.BOOK).setDisplayName("&6Kit Editor").setLore(" ").create(),
                null,
                null,
                null,
                new ItemBuilder(Material.EYE_OF_ENDER).setDisplayName("&eCreate a team").setLore(" ").create(),
                null,
                null,
                new ItemBuilder(Material.IRON_SWORD).setDisplayName("&9Un-Ranked Queue").setLore(" ").create(),
                new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName("&aRanked Queue").setLore(" ").create()
        };
    }

    public static ItemStack[] getTeamLeaderInventory() {
        return new ItemStack[]{
                new ItemBuilder(Material.NETHER_STAR).setDisplayName("&9Your team").setLore(" ").create(),
                new ItemBuilder(Material.SKULL_ITEM).setDurability(3).setDisplayName("&eTeam info").setLore(" ").create(),
                null,
                new ItemBuilder(Material.FIRE).setDisplayName("&cDisband team").setLore(" ").create(),
                null,
                new ItemBuilder(Material.EYE_OF_ENDER).setDisplayName("&bTeam events").setLore(" ").create(),
                null,
                null,
                null
                //new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName("&6Fight other teams").setLore(" ").create()
        };
    }

    public static ItemStack[] getTeamMemberInventory() {
        return new ItemStack[]{
                new ItemBuilder(Material.NETHER_STAR).setDisplayName("&9Your team").setLore(" ").create(),
                new ItemBuilder(Material.SKULL_ITEM).setDurability(3).setDisplayName("&eTeam info").setLore(" ").create(),
                null,
                null,
                null,
                null,
                null,
                null,
                null
        };
    }

    /*public static void mountPlayerIntoEntity(Player player) {
        WorldServer worldServer = ((CraftWorld) player.getLocation().getWorld()).getHandle();
        EntityBat bat = new EntityBat(worldServer);

        bat.setLocation(player.getLocation().getX() + 0.5, player.getLocation().getY() + 2, player.getLocation().getZ() + 0.5, 0, 0);
        bat.setHealth(bat.getMaxHealth());
        bat.setInvisible(true);
        bat.d(0);
        bat.setAsleep(true);
        bat.setAirTicks(10);
        bat.setSneaking(false);

        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(bat);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

        PacketPlayOutAttachEntity attach = new PacketPlayOutAttachEntity(0, ((CraftPlayer) player).getHandle(), bat);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(attach);

        packets.put(player, bat.getId());
    }*/
}
