package me.redis.practice.arena.commands.arguments;

import me.redis.practice.Practice;
import me.redis.practice.arena.Arena;
import me.redis.practice.utils.LocationUtils;
import me.redis.practice.utils.command.CommandArgument;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListArenasArgument extends CommandArgument {
    public ListArenasArgument() {
        super("list");
    }

    @Override
    public String getUsage(String label) {
        return ChatColor.RED + "/" + label + ' ' + getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.GOLD + "List of arenas:");
        for (Arena arena : Practice.getPlugin().getArenaManager().getArenas().values()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a" + arena.getName() + " &7(" + StringUtils.join(arena.getAuthors(), ", ") + ") &7- " + (arena.isUsable() ? "&aUsable" : "&cNot usable") + " &7-> " + arena.getPos1().getX() + ", " + arena.getPos1().getZ()));
        }

        return true;
    }
}
