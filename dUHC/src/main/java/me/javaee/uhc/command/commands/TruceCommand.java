package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.team.UHCTeam;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TruceCommand extends BaseCommand {
    private List<Player> list = new ArrayList<>();

    public TruceCommand() {
        super("truce", Collections.singletonList("trucerino"), false, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
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
                    return;
                }
                if (list.contains(player)) {
                    player.sendMessage(ChatColor.RED + "You have to wait 1 minute.");
                    return;
                }

                for (Player online : UHC.getInstance().getGameManager().getModerators()) {
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

                if (UHC.getInstance().getGameManager().getHost() != null) {
                    UHC.getInstance().getGameManager().getHost().sendMessage(ChatColor.translateAlternateColorCodes("&7&m--------------------------"));
                    UHC.getInstance().getGameManager().getHost().sendMessage(ChatColor.translateAlternateColorCodes(" &a&lTruce Alert &7(By: " + player.getName() + ")"));
                    UHC.getInstance().getGameManager().getHost().sendMessage("");
                    TextComponent text = new TextComponent(ChatColor.translateAlternateColorCodes(" &7- &e" + truce1.getName()));
                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + "Click here to teleport to " + truce1.getName()).create()));
                    text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + truce1.getName()));
                    UHC.getInstance().getGameManager().getHost().spigot().sendMessage(text);
                    TextComponent txt2 = new TextComponent(ChatColor.translateAlternateColorCodes(" &7- &e" + truce2.getName()));
                    txt2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + "Click here to teleport to " + truce2.getName()).create()));
                    txt2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + truce2.getName()));
                    UHC.getInstance().getGameManager().getHost().spigot().sendMessage(txt2);
                    UHC.getInstance().getGameManager().getHost().sendMessage(ChatColor.translateAlternateColorCodes("&7&m--------------------------"));

                    UHC.getInstance().getGameManager().getHost().playSound(UHC.getInstance().getGameManager().getHost().getLocation(), Sound.ANVIL_LAND, 50, 50);
                }

                list.add(player);
                player.sendMessage(ChatColor.translateAlternateColorCodes("&cYou have reported '&7" + truce1.getName() + ", " + truce2.getName() + "' &cfor truce."));

                Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> list.remove(player), 20 * 60);
            }
        }
    }

    @Override
    public String getDescription() {
        return "Truce report command";
    }
}
