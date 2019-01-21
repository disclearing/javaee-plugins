package me.javaee.meetup.commands;

import me.javaee.meetup.Meetup;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TruceCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "/" + label + " <truce1> <truce2>");
        } else {
            Player truce1 = Bukkit.getPlayer(args[0]);
            Player truce2 = Bukkit.getPlayer(args[1]);

            if (truce1 == null || truce2 == null) {
                player.sendMessage(ChatColor.RED + "One of the players is not online.");
            } else {
                if (truce1 == truce2) {
                    player.sendMessage(ChatColor.RED + "You can't report the same person twice.");
                    return true;
                }

                for (Player online : Meetup.getPlugin().getGameManager().getModerators()) {
                    online.sendMessage(ChatColor.translateAlternateColorCodes("&7&m--------------------------"));
                    online.sendMessage(ChatColor.translateAlternateColorCodes(" &a&lTruce Alert &7(By: " + player.getName() + ")"));
                    online.sendMessage("");
                    TextComponent text = new TextComponent(ChatColor.translateAlternateColorCodes(" &7- &e" + truce1.getName()));
                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + "Click here to teleport to " + truce1.getName()).create()));
                    text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + truce1.getName()));
                    online.spigot().sendMessage(text);
                    TextComponent txt2 = new TextComponent(ChatColor.translateAlternateColorCodes(" &7- &e" + truce2.getName()));
                    txt2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + "Click here to teleport to " + truce2.getName()).create()));
                    txt2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + truce2.getName()));
                    online.spigot().sendMessage(txt2);
                    online.sendMessage(ChatColor.translateAlternateColorCodes("&7&m--------------------------"));

                    online.playSound(online.getLocation(), Sound.ANVIL_LAND, 50, 50);
                }

                player.sendMessage(ChatColor.translateAlternateColorCodes("&cYou have reported '&7" + truce1.getName() + ", " + truce2.getName() + "' &cfor truce."));
            }
        }
        return true;
    }
}
