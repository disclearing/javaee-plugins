package me.javaee.meetup.commands;

import me.javaee.meetup.Meetup;
import me.javaee.meetup.enums.GameState;
import me.javaee.meetup.kit.Kit;
import me.javaee.meetup.profile.Profile;
import me.javaee.meetup.profile.ProfileUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class RerollCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (Meetup.getPlugin().getGameManager().getGameState() == GameState.WAITING) {
                Profile profile = ProfileUtils.getInstance().getProfile(player.getUniqueId());

                if (profile.getRerolls() <= 0) {
                    player.sendMessage(ChatColor.RED + "You don't have any rerolls.");
                } else {
                    profile.setRerolls(profile.getRerolls() - 1);
                    profile.save(true);
                    player.sendMessage(ChatColor.GREEN + "You have used a reroll. You have " + profile.getRerolls() + " left.");

                    Kit kit = Meetup.getPlugin().getKitManager().getKits().get(new Random().nextInt(19));
                    player.getInventory().setContents(kit.getInventory());
                    player.getInventory().setArmorContents(kit.getArmor());
                    player.getInventory().addItem(new ItemStack(Material.DIAMOND_PICKAXE));
                }
            } else {
                player.sendMessage(ChatColor.RED + "You must be waiting.");
            }
        }
        return true;
    }
}
