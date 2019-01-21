package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class LagCommand extends BaseCommand {
    public LagCommand() {
        super("lag", Arrays.asList("gc", "lagg"), true, false);
    }

    DecimalFormat format = new DecimalFormat("#.##");

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Integer total = 0;

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                total += 1;
            }
        }

        long max = Runtime.getRuntime().maxMemory() / 1024 / 1024;
        long free = Runtime.getRuntime().freeMemory() / 1024 / 1024;
        long used = max - free;

        sender.sendMessage(ChatColor.translateAlternateColorCodes("&7&m----------------------------------------"));
        sender.sendMessage(ChatColor.GOLD + "TPS from last 1m, 5m, 15m: " + getFormattedTps(Bukkit.spigot().getTPS()[0]) + ChatColor.GOLD + ", " + getFormattedTps(Bukkit.spigot().getTPS()[1]) + ChatColor.GOLD + ", " + getFormattedTps(Bukkit.spigot().getTPS()[2]));
        sender.sendMessage(ChatColor.GOLD + "Server Performance: " + ChatColor.GREEN + format.format(getLag()) + "%");
        //sender.sendMessage(ChatColor.GOLD + "Server Tick: " + ChatColor.GREEN + "TODO");
        sender.sendMessage(ChatColor.GOLD + "Random Access Memory: " + ChatColor.GREEN + "Free: " + free + "MB, Used: " + used + "MB");
        sender.sendMessage(ChatColor.GOLD + "Online Players: " + ChatColor.GREEN + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
        sender.sendMessage(ChatColor.GOLD + "Uptime: " + ChatColor.GREEN + DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime(), true, true));
        sender.sendMessage(ChatColor.GOLD + "Entities: " + ChatColor.GREEN + total + "");
        sender.sendMessage(ChatColor.translateAlternateColorCodes("&7&m----------------------------------------"));
    }

    @Override
    public String getDescription() {
        return "Sees the lag on the server.";
    }

    public double getLag() {
        return (Bukkit.spigot().getTPS()[0] / 20 * 100 > 100 ? 100 : Bukkit.spigot().getTPS()[0] / 20 * 100);
    }

    private long lastPoll = System.currentTimeMillis() - 3000;

    public String getFormattedTps(double tps) {
        if (tps > 20) {
            return ChatColor.DARK_GREEN + "*20";
        } else if (tps <= 20.0 && tps > 15) {
            return ChatColor.GREEN .toString() + format.format(tps);
        } else if (tps <= 15 && tps > 10) {
            return ChatColor.YELLOW.toString() + format.format(tps);
        } else if (tps <= 10 && tps > 5) {
            return ChatColor.RED.toString() + format.format(tps);
        } else if (tps <= 5) {
            return ChatColor.DARK_RED.toString() + format.format(tps);
        } else {
            return "0";
        }
    }
}
