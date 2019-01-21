package me.javaee.uhc.tasks;

import me.javaee.uhc.UHC;
import me.javaee.uhc.combatlogger.CombatLogger;
import me.javaee.uhc.database.profile.Profile;
import me.javaee.uhc.database.profile.ProfileUtils;
import me.javaee.uhc.listeners.misc.EndListener;
import me.javaee.uhc.team.UHCTeam;
import me.javaee.uhc.utils.Configurator;
import me.javaee.uhc.utils.InventorySerialization;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DisconnectedTask extends BukkitRunnable {
    private Player player;

    public DisconnectedTask(Player player) {
        this.player = player;
        Profile profile = ProfileUtils.getInstance().getProfile(player.getUniqueId());

        profile.setAFKTimeLeft(System.currentTimeMillis());
    }

    @Override
    public void run() {
        Profile profile = ProfileUtils.getInstance().getProfile(player.getUniqueId());
        if (profile.getAFKTimeLeft() != 0) {
            CombatLogger loggerNPC = UHC.getInstance().getCombatLoggerManager().getByPlayer(player);

            if (loggerNPC != null) {
                UHC.getInstance().getGameManager().getAlivePlayers().remove(player.getUniqueId());

                InventorySerialization.saveInventoryToProfile(profile, player.getInventory().getContents());
                InventorySerialization.saveArmorToProfile(profile, player.getInventory().getArmorContents());

                profile.setDeathLocation(player.getLocation().getX() + ";" + player.getLocation().getZ());
                profile.setDeaths(profile.getDeaths() + 1);
                profile.setDead(true);
                profile.save(true);

                UHCTeam team = UHCTeam.getByUUID(player.getUniqueId());
                if (team != null) {
                    int newDtr = team.getDtr() - 1;

                    team.setDtr(newDtr);

                    if (team.getDtr() < 1) {
                        UHC.getInstance().getTeams().remove(team);
                    }
                }

                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&c" + loggerNPC.getEntity().getCustomName() + "&7[&f" + ProfileUtils.getInstance().getProfile(loggerNPC.getUniqueId()).getMatchKills() + "&7] (Disconnected) &edidn't relog back in time."));
                loggerNPC.getEntity().remove();
                UHC.getInstance().getCombatLoggerManager().combatLoggers.remove(loggerNPC);
            }
        }
    }

}
