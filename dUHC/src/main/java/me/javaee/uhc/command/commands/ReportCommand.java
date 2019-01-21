package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReportCommand extends BaseCommand {
    private List<Player> list = new ArrayList<>();

    public ReportCommand() {
        super("report", Arrays.asList("reportplayer"), false, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "/" + label + " <player> <reason>");
        } else {
            String reason = StringUtils.join(args, " ", 1, args.length);
            String reporter = player.getName();
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(ChatColor.RED + "Player with name or uuid '" + args[0] + "' not found.");
            } else {
                String reported = target.getName();

                if (target == player) {
                    player.sendMessage(ChatColor.RED + "You can't report yourself.");
                    return;
                }

                if (list.contains(player)) {
                    player.sendMessage(ChatColor.RED + "You need to wait 1 minute to report another player.");
                    return;
                }

                player.sendMessage(ChatColor.translateAlternateColorCodes("&cYou have reported " + target.getName() + " for '&7" + reason + "&7'&c."));
                list.add(player);

                Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> list.remove(player), 20 * 60);

                for (Player mods : UHC.getInstance().getGameManager().getModerators()) {
                    mods.sendMessage(ChatColor.translateAlternateColorCodes("&7&m--------------------------"));
                    mods.sendMessage(ChatColor.translateAlternateColorCodes(" &c&lReport Alert &7(By: " + reporter + ")"));
                    mods.sendMessage(ChatColor.translateAlternateColorCodes(""));
                    TextComponent text = new TextComponent(ChatColor.translateAlternateColorCodes(" &cReported&7: " + reported));
                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + "Click here to teleport to " + reported).create()));
                    text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + reported));
                    mods.spigot().sendMessage(text);
                    mods.sendMessage(ChatColor.translateAlternateColorCodes(" &cReason: &7" + reason));
                    mods.sendMessage(ChatColor.translateAlternateColorCodes("&7&m--------------------------"));

                    mods.playSound(mods.getLocation(), Sound.CHICKEN_EGG_POP, 50, 50);
                }

                if (UHC.getInstance().getGameManager().getHost() != null) {
                    Player host = UHC.getInstance().getGameManager().getHost();

                    host.sendMessage(ChatColor.translateAlternateColorCodes("&7&m--------------------------"));
                    host.sendMessage(ChatColor.translateAlternateColorCodes(" &c&lReport Alert &7(By: " + reporter + ")"));
                    host.sendMessage(ChatColor.translateAlternateColorCodes(""));
                    TextComponent text = new TextComponent(ChatColor.translateAlternateColorCodes(" &cReported&7: " + reported));
                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + "Click here to teleport to " + reported).create()));
                    text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + reported));
                    host.spigot().sendMessage(text);
                    host.sendMessage(ChatColor.translateAlternateColorCodes(" &cReason: &7" + reason));
                    host.sendMessage(ChatColor.translateAlternateColorCodes("&7&m--------------------------"));

                    host.playSound(host.getLocation(), Sound.CHICKEN_EGG_POP, 50, 50);
                }
            }
        }
    }

    @Override
    public String getDescription() {
        return "Reports a player";
    }
}
