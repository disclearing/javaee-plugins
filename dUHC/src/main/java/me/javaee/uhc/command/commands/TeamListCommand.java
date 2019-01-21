package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.database.profile.ProfileUtils;
import me.javaee.uhc.team.UHCTeam;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class TeamListCommand extends BaseCommand {
    public TeamListCommand() {
        super("teamlist", Arrays.asList("tl", "listteam", "lt"), false, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (UHC.getInstance().getConfigurator().getIntegerOption(UHC.CONFIG_OPTIONS.TEAMSIZE.name()).getValue() >= 2) {
            if (args.length == 0) {
                if (UHCTeam.getByUUID(player.getUniqueId()) == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a team.");
                    return;
                }

                sendTeamInfo(UHCTeam.getByUUID(player.getUniqueId()), player);
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("list")) {
                    for (UHCTeam team : UHC.getInstance().getTeams()) {
                        player.sendMessage(team.getNumber() + ". " + team.getPlayerList() + " dtr: " + team.getDtr());
                    }
                    return;
                }

                if (isInt(args[0])) {
                    UHCTeam team = UHCTeam.getByNumber(Integer.parseInt(args[0]));

                    if (team == null) {
                        player.sendMessage(ChatColor.RED + "That team does not exist.");
                    } else {
                        sendTeamInfo(team, player);
                    }
                } else {
                    Player target = Bukkit.getPlayer(args[0]);

                    if (target == null) {
                        sender.sendMessage(ChatColor.RED + "Player with name '" + args[0] + "' not found.");
                    } else {
                        if (UHCTeam.getByUUID(target.getUniqueId()) != null) {
                            sendTeamInfo(UHCTeam.getByUUID(target.getUniqueId()), player);
                        } else {
                            player.sendMessage(ChatColor.RED + "That team does not exist.");
                        }
                    }
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "This is a free for all game.");
        }
    }

    @Override
    public String getDescription() {
        return "It gives you info of a specific team";
    }

    private void sendMessage(Player player, String text) {
        player.sendMessage(ChatColor.translateAlternateColorCodes(text));
    }

    public boolean isInt(String a) {
        try {
            int i = Integer.parseInt(a);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public int getHealth(UUID uuid) {
        if (Bukkit.getPlayer(uuid) != null) {
            return (int) Bukkit.getPlayer(uuid).getHealth();
        } else {
            return 0;
        }
    }

    public void sendTeamInfo(UHCTeam uhcTeam, Player player) {
        ArrayList<String> teamList = new ArrayList<>();
        teamList.clear();

        sendMessage(player, "&7&m------------------------------");
        sendMessage(player, "&cTeam&7: &7#" + uhcTeam.getNumber());
        sendMessage(player, "");
        sendMessage(player, "&eLeader: &7" + Bukkit.getOfflinePlayer(uhcTeam.getLeader()).getName());
        sendMessage(player, "&eTeam Kills: &7" + uhcTeam.getKills());

        for (UUID teamPlayers : uhcTeam.getPlayerList()) {
            if (ProfileUtils.getInstance().getProfile(teamPlayers).isDead()) {
                teamList.add(Bukkit.getOfflinePlayer(teamPlayers).getName() + "(" + ChatColor.RED + "Dead" + ChatColor.GRAY + ")");
            } else {
                teamList.add(Bukkit.getOfflinePlayer(teamPlayers).getName() + "(" + ChatColor.RED + getHealth(teamPlayers) + " ♥" + ChatColor.GRAY + ")");
            }
        }

        sendMessage(player, "&eMembers: &7" + StringUtils.join(teamList, ChatColor.YELLOW + ", " + ChatColor.GRAY));
        sendMessage(player, "&7&m------------------------------");
    }
}
