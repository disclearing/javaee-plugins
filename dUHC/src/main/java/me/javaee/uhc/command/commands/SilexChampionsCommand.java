package me.javaee.uhc.command.commands;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.database.profile.Profile;
import me.javaee.uhc.database.profile.ProfileUtils;
import me.javaee.uhc.utils.MojangNameFetcher;
import net.minecraft.server.v1_7_R4.WhiteList;
import net.silexpvp.nightmare.profile.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;

import java.util.*;

public class SilexChampionsCommand extends BaseCommand {
    public SilexChampionsCommand() {
        super("silexchampions", Arrays.asList("schampions, scwl"), true, false);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(UHC.getInstance(), () -> {
            for (Profile profile : ProfileUtils.getInstance().getAllProfiles()) {
                if (profile.getWinnedGames() > 0) {
                    UHC.getInstance().getChampions().add(profile.getUuid());
                    try {
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&e'&7" + profile.getLastName() + "&e' has been whitelisted with " + profile.getWinnedGames() + " win/s."));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (UHC.getInstance().getChampions().contains(profile.getUuid())) continue;

                if (profile.getKills() > 50) {
                    UHC.getInstance().getChampions().add(profile.getUuid());
                    try {
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&e'&7" + profile.getLastName() + "&e' has been whitelisted with " + profile.getKills() + " kill/s."));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public String getDescription() {
        return "Whitelists people with 50 kills or more and 1 win or more.";
    }
}
