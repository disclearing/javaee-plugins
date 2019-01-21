package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;

public class SquareCommand extends BaseCommand {
    public SquareCommand() {
        super("square", Collections.singletonList("cuadrado"), true, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (args.length == 1) {
            if (isInt(args[0])) {
                int radius = Integer.parseInt(args[0]);

                Bukkit.getScheduler().runTask(UHC.getInstance(), () -> {
                    getBlocks(player.getLocation().getBlock(), radius).forEach(location -> location.getLocation().getBlock().setType(Material.STONE));

                    player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have created a square with &b" + args[0] + " &eradius!"));
                });
            } else {
                player.sendMessage(ChatColor.RED + "Must be an integer.");
            }
        }
    }

    @Override
    public String getDescription() {
        return "Makes an square around you";
    }

    public boolean isInt(String integer) {
        try {
            Integer a = Integer.parseInt(integer);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static ArrayList<Block> getBlocks(Block start, int radius) {
        ArrayList<Block> blocks = new ArrayList<>();

        for (double x = start.getLocation().getX() - radius; x <= start.getLocation().getX() + radius; x++) {
            for (double y = start.getLocation().getY(); y <= start.getLocation().getY(); y++) {
                for (double z = start.getLocation().getZ() - radius; z <= start.getLocation().getZ() + radius; z++) {
                    Location loc = new Location(start.getWorld(), x, y, z);

                    blocks.add(loc.getBlock());
                }
            }
        }
        return blocks;
    }
}
