package me.redis.practice.duel.argument;

import me.redis.practice.Practice;
import me.redis.practice.enums.ProfileStatus;
import me.redis.practice.ladders.Ladder;
import me.redis.practice.listeners.MenuListener;
import me.redis.practice.profile.Profile;
import me.redis.practice.utils.ItemBuilder;
import me.redis.practice.utils.SerializationUtils;
import me.redis.practice.utils.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DuelPlayerArgument extends CommandArgument {
    public DuelPlayerArgument() {
        super("send");
    }

    @Override
    public String getUsage(String label) {
        return ChatColor.RED + "/" + label + " " + getName() + " <name>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

            if (args.length != 2) {
                player.sendMessage(getUsage(label));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (profile.getStatus() != ProfileStatus.LOBBY) {
                return true;
            }

            if (player == target) {
                return true;
            }

            if (target == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlayer with name '&7" + args[1] + "&c' does not exist."));
                return true;
            }

            Profile targetProfile = Practice.getPlugin().getProfileManager().getProfile(target.getUniqueId());

            if (targetProfile.getStatus() != ProfileStatus.LOBBY) {
                player.sendMessage(ChatColor.RED + target.getName() + " is not in the spawn.");
                return true;
            }

            if (targetProfile.hasRequest(player)) {
                player.sendMessage(ChatColor.RED + "You have already requested that player a match.");
                return true;
            }

            MenuListener.selectedPlayer.put(player.getUniqueId(), target.getUniqueId());
            open(player);
        }
        return true;
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.BLUE + "Send a duel");

        for (Ladder ladder : Practice.getPlugin().getLadderManager().getLadders().values()) {
            ItemStack item = SerializationUtils.itemStackFromString(ladder.getIcon());

            inventory.addItem(new ItemBuilder(item).setDisplayName("&a" + ladder.getName()).create());
        }

        player.openInventory(inventory);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}