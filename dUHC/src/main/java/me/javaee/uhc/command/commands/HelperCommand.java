package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.database.profile.ProfileUtils;
import me.javaee.uhc.enums.GameState;
import me.javaee.uhc.team.UHCTeam;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class HelperCommand extends BaseCommand {
    public HelperCommand() {
        super("helper", Arrays.asList("helper"), true, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        List<Player> helpers = UHC.getInstance().getGameManager().getHelpers();

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
        } else {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                sender.sendMessage(org.bukkit.ChatColor.RED + "Player with name '" + args[0] + "' not found.");
            } else {
                if (helpers.contains(target)) {
                    helpers.remove(target);
                    sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes("&f" + target.getName() + "&6 has been removed from the helpers list."));
                } else {
                    if (!target.hasPermission("litebans.warn")) {
                        sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes("&c" + target.getName() + " could not be made helper. He is not a helper."));
                        return;
                    }

                    helpers.add(target);
                    sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes("&f" + target.getName() + "&6 has been added to the helpers list."));

                    if (UHC.getInstance().getGameManager().getGameState() == GameState.INGAME) {
                        if (UHC.getInstance().getGameManager().getAlivePlayers().contains(target.getUniqueId())) {
                            UHC.getInstance().getGameManager().getAlivePlayers().remove(target.getUniqueId());
                        }
                        UHC.getInstance().getStaffModeManager().setStaffMode(target);
                    }

                    ProfileUtils.getInstance().getProfile(target.getUniqueId()).setDead(true);
                    ProfileUtils.getInstance().getProfile(target.getUniqueId()).save(true);

                    UHCTeam team = UHCTeam.getByUUID(target.getUniqueId());

                    if (team != null) {
                        if (UHC.getInstance().getGameManager().getGameState() == GameState.WAITING) {
                            team.getPlayerList().remove(target.getUniqueId());
                        } else {
                            team.setDtr(team.getDtr() - 1);
                            team.getPlayerList().remove(target.getUniqueId());
                        }
                    }

                    Bukkit.getOnlinePlayers().forEach(online -> {
                        if (!online.hasPermission("litebans.warn")) online.hidePlayer((Player) sender);
                    });
                }
            }
        }
    }

    @Override
    public String getDescription() {
        return "Puts someone in the helpers list";
    }
}
