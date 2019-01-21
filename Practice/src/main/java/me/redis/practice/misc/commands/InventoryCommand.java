package me.redis.practice.misc.commands;

import me.redis.practice.Practice;
import me.redis.practice.enums.ProfileStatus;
import me.redis.practice.match.cache.MatchCache;
import me.redis.practice.profile.Profile;
import me.redis.practice.utils.command.ExecutableCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InventoryCommand extends ExecutableCommand {
    public InventoryCommand() {
        super("_");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /_ <player>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (target == null) {
            return true;
        }

        if (profile.getStatus() != ProfileStatus.LOBBY) {
            return true;
        }

        if (MatchCache.inventories.containsKey(target.getUniqueId())) {
            player.openInventory(MatchCache.inventories.get(target.getUniqueId()));
        }

        return true;
    }
}
