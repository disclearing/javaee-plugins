package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import net.silexpvp.nightmare.util.JavaUtils;
import net.silexpvp.nightmare.util.command.ExecutableCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldLoaderCommand extends BaseCommand {
    public static List<Chunk> chunks = new ArrayList<>();
    public static int counter = 0;

    public static boolean generating = false;

    public WorldLoaderCommand() {
        super("loadworld", Arrays.asList(""), true, true);
    }

    public void addAllChunks(int radius) {
        Bukkit.getScheduler().runTask(UHC.getInstance(), () -> {
            for (int x = 0; x < radius; x++) {
                for (int z = 0; z < radius; z++) {
                    if (!chunks.contains(Bukkit.getWorld("world").getChunkAt(x, z))) {
                        chunks.add(Bukkit.getWorld("world").getChunkAt(x, z));
                    }
                }
            }
        });
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        Integer radius = JavaUtils.tryParseInteger(args[0]);

        if (radius != null) {
            generating = true;

            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&6Adding &f" + radius + " &6chunks."));
            addAllChunks(radius);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&6Chunks added."));
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(ChatColor.GRAY + "Now loading " + radius + " chunks.");

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (counter > chunks.size()) {
                        generating = false;
                        cancel();
                        return;
                    }

                    Bukkit.getWorld("world").loadChunk(chunks.get(counter));

                    counter++;
                }
            }.runTaskTimer(UHC.getInstance(), 0L, 10L);
        }
    }

    @Override
    public String getDescription() {
        return "Loads all the chunks";
    }
}
