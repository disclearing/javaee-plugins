package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.enums.GameState;
import me.javaee.uhc.events.TeamCreateEvent;
import me.javaee.uhc.events.TeamJoinEvent;
import me.javaee.uhc.events.TeamLeaveEvent;
import me.javaee.uhc.team.UHCTeam;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class TeamCommand extends BaseCommand {
    public TeamCommand() {
        super("team", Arrays.asList("t", "f", "faction", "factions"), false, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;


        if (UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue() > 1) {
            if (UHC.getInstance().getGameManager().getHost() == player) {
                if (args.length == 0) {
                    sendHelp(player);
                } else {
                    if (args[0].equalsIgnoreCase("remove")) {
                        if (UHCTeam.getByNumber(Integer.parseInt(args[1])) != null) {
                            UHC.getInstance().getTeams().remove(UHCTeam.getByNumber(Integer.parseInt(args[1])));
                        } else {
                            player.sendMessage(ChatColor.DARK_RED + "That team does not exist.");
                        }
                        return;
                    }
                }

                player.sendMessage(ChatColor.RED + "You can't create a team if you are staff.");
                return;
            }

            if (UHC.getInstance().getGameManager().getGameState() != GameState.WAITING) {
                player.sendMessage(ChatColor.RED + "You can only use this command on the lobby.");
                return;
            }

            if (UHC.getInstance().getGameManager().getModerators().contains(player)) {
                player.sendMessage(ChatColor.RED + "You can't create a team if you are staff.");
                return;
            }

            if (UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue() == 50) {
                if (args.length == 3 && args[0].equalsIgnoreCase("setname")) {
                    UHCTeam team = UHCTeam.getByName(args[1]);

                    if (team == null) {
                        player.sendMessage(ChatColor.RED + "That team does not exist.");
                        return;
                    }

                    team.setDisplayName(args[2]);
                    player.sendMessage(ChatColor.translateAlternateColorCodes("&e" + team.getName() + "'s team name is now: " + team.getDisplayName() + "."));
                    return;
                }
                if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
                    UHCTeam team = UHCTeam.getByDisplayName(args[1]);

                    if (team == null) {
                        player.sendMessage(ChatColor.RED + "That team does not exist.");
                        return;
                    }

                    if (UHCTeam.getByUUID(player.getUniqueId()) != null) {
                        player.sendMessage(ChatColor.RED + "You already have a team.");
                        return;
                    }

                    if (team.getPlayerList().size() >= 50) {
                        player.sendMessage(ChatColor.RED + "You can't join that team, it's full.");
                        return;
                    }

                    team.getPlayerList().add(player.getUniqueId());

                    team.getPlayerList().forEach(uuid -> {
                        Player teamPlayers = Bukkit.getPlayer(uuid);

                        if (teamPlayers != null) {
                            teamPlayers.sendMessage(ChatColor.translateAlternateColorCodes("&6" + player.getName() + "&e has joined the team. &7(" + team.getPlayerList().size() + "/50)"));
                        }
                    });
                } else {
                    player.sendMessage(ChatColor.RED + "You can only join a team.");
                }

                return;
            }

            if (args.length == 0) {
                sendHelp(player);
            } else {
                if (args.length == 1 && args[0].equalsIgnoreCase("create")) {
                    if (UHCTeam.getByUUID(player.getUniqueId()) == null) {
                        UHCTeam team = new UHCTeam(player.getUniqueId());

                        UHC.getInstance().getTeams().add(team);
                        Bukkit.getPluginManager().callEvent(new TeamCreateEvent(player, team));
                    } else {
                        player.sendMessage(ChatColor.RED + "You are already in a team.");
                    }
                } else if (args.length == 2 && args[0].equalsIgnoreCase("invite")) {
                    if (UHCTeam.getByUUID(player.getUniqueId()) == null) {
                        player.sendMessage(ChatColor.RED + "You are not in a team.");
                    } else {
                        if (UHCTeam.getByUUID(player.getUniqueId()).getLeader() != player.getUniqueId()) {
                            player.sendMessage(ChatColor.RED + "You are not the leader of the team.");
                        } else {
                            Player target = Bukkit.getPlayer(args[1]);

                            if (target == null) {
                                sender.sendMessage(ChatColor.RED + "Player with name '" + args[1] + "' not found.");
                            } else {
                                if (UHCTeam.getByUUID(player.getUniqueId()).getPlayerList().contains(target.getUniqueId())) {
                                    player.sendMessage(ChatColor.RED + "That player is already on your team.");
                                    return;
                                }

                                UHCTeam.getByUUID(player.getUniqueId()).getInviteList().add(target.getUniqueId());

                                target.sendMessage(ChatColor.translateAlternateColorCodes("&6" + player.getName() + "&e has invited you to join their team."));
                                ComponentBuilder builder = new ComponentBuilder("");
                                builder.append("Type '").color(net.md_5.bungee.api.ChatColor.YELLOW);
                                builder.append("/team join " + player.getName()).color(net.md_5.bungee.api.ChatColor.WHITE);
                                builder.append("' or ").color(net.md_5.bungee.api.ChatColor.YELLOW);
                                builder.append("Click Here").color(net.md_5.bungee.api.ChatColor.GREEN).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join " + player.getName() + ".").color(net.md_5.bungee.api.ChatColor.GREEN).create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/team join " + player.getName()));
                                builder.append(" to participate.").color(net.md_5.bungee.api.ChatColor.YELLOW);
                                builder.retain(ComponentBuilder.FormatRetention.FORMATTING);

                                BaseComponent[] components = builder.create();
                                target.spigot().sendMessage(components);

                                for (UUID players : UHCTeam.getByUUID(player.getUniqueId()).getPlayerList()) {
                                    if (Bukkit.getPlayer(players) != null) {
                                        Bukkit.getPlayer(players).sendMessage(ChatColor.translateAlternateColorCodes("&6" + target.getName() + " &ehas been invited to the team."));
                                    }
                                }
                            }
                        }
                    }
                } else if (args.length == 2 && args[0].equalsIgnoreCase("join")) {

                    Player target = Bukkit.getPlayer(args[1]);

                    if (UHCTeam.getByUUID(target.getUniqueId()) == null) {
                        player.sendMessage(ChatColor.RED + "That team does not exist.");
                    } else {
                        if (UHCTeam.getByUUID(player.getUniqueId()) != null) {
                            player.sendMessage(ChatColor.RED + "You already have a team.");
                            return;
                        }

                        if (UHCTeam.getByUUID(target.getUniqueId()).getInviteList().contains(player.getUniqueId())) {
                            if (UHCTeam.getByUUID(target.getUniqueId()).getPlayerList().size() >= UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue()) {
                                player.sendMessage(ChatColor.RED + "That team is currently full.");
                            } else {
                                UHCTeam.getByUUID(target.getUniqueId()).getPlayerList().add(player.getUniqueId());

                                for (UUID players : UHCTeam.getByUUID(target.getUniqueId()).getPlayerList()) {
                                    if (Bukkit.getPlayer(players) != null) {
                                        Bukkit.getPlayer(players).sendMessage(ChatColor.translateAlternateColorCodes("&6" + player.getName() + " &ehas joined the team."));
                                    }
                                }

                                UHCTeam.getByUUID(target.getUniqueId()).getInviteList().remove(player.getUniqueId());

                                TeamJoinEvent teamJoinEvent = new TeamJoinEvent(player, UHCTeam.getByUUID(player.getUniqueId()));
                                Bukkit.getPluginManager().callEvent(teamJoinEvent);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "That team has not invited you.");
                        }
                    }
                } else if (args.length == 2 && args[0].equalsIgnoreCase("kick")) {
                    Player target = Bukkit.getPlayer(args[1]);

                    if (target == null) {
                        sender.sendMessage(ChatColor.RED + "Player with name '" + args[1] + "' not found.");
                    } else {
                        if (UHCTeam.getByUUID(player.getUniqueId()) == null) {
                            player.sendMessage(ChatColor.RED + "You are not in a team.");
                        } else {
                            if (UHCTeam.getByUUID(player.getUniqueId()).getLeader() != player.getUniqueId()) {
                                player.sendMessage(ChatColor.RED + "You are not the leader of the team.");
                            } else {
                                if (!UHCTeam.getByUUID(player.getUniqueId()).getPlayerList().contains(target.getUniqueId())) {
                                    player.sendMessage(ChatColor.RED + "You can't kick that player, he is not in your team.");
                                } else {
                                    UHCTeam.getByUUID(player.getUniqueId()).getPlayerList().remove(target.getUniqueId());
                                    UHCTeam team = UHCTeam.getByUUID(player.getUniqueId());

                                    for (UUID players : UHCTeam.getByUUID(player.getUniqueId()).getPlayerList()) {
                                        if (Bukkit.getPlayer(players) != null) {
                                            Bukkit.getPlayer(players).sendMessage(ChatColor.translateAlternateColorCodes("&6" + target.getName() + " &ehas been kicked from the team."));
                                        }
                                    }

                                    TeamLeaveEvent teamLeaveEvent = new TeamLeaveEvent(target, team);
                                    Bukkit.getPluginManager().callEvent(teamLeaveEvent);

                                    target.sendMessage(ChatColor.RED + "You have been kicked from your team.");
                                }
                            }
                        }
                    }
                } else if (args.length == 1 && args[0].equalsIgnoreCase("leave")) {
                    if (UHCTeam.getByUUID(player.getUniqueId()) == null) {
                        player.sendMessage(ChatColor.RED + "You are not in a team");
                    } else {
                        if (UHCTeam.getByUUID(player.getUniqueId()).getLeader() == player.getUniqueId()) {
                            player.sendMessage(ChatColor.RED + "You can't leave the team, you must disband it.");
                        } else {
                            for (UUID players : UHCTeam.getByUUID(player.getUniqueId()).getPlayerList()) {
                                if (Bukkit.getPlayer(players) != null) {
                                    Bukkit.getPlayer(players).sendMessage(ChatColor.translateAlternateColorCodes("&6" + player.getName() + " &ehas left the team."));
                                }
                            }
                            UHCTeam team = UHCTeam.getByUUID(player.getUniqueId());
                            UHCTeam.getByUUID(player.getUniqueId()).getPlayerList().remove(player.getUniqueId());

                            TeamLeaveEvent teamLeaveEvent = new TeamLeaveEvent(player, team);
                            Bukkit.getPluginManager().callEvent(teamLeaveEvent);
                        }
                    }
                } else if (args.length == 1 && args[0].equalsIgnoreCase("disband")) {
                    if (UHCTeam.getByUUID(player.getUniqueId()) == null) {
                        player.sendMessage(ChatColor.RED + "You are not in a team");
                    } else {
                        UHCTeam team = UHCTeam.getByUUID(player.getUniqueId());

                        if (team.getLeader() != player.getUniqueId()) {
                            player.sendMessage(ChatColor.RED + "You must be the leader of the team.");
                        } else {
                            for (UUID players : team.getPlayerList()) {
                                if (Bukkit.getPlayer(players) != null) {
                                    Bukkit.getPlayer(players).sendMessage(ChatColor.translateAlternateColorCodes("&6Your team has been disbanded."));
                                }
                            }
                            UHC.getInstance().getTeams().remove(team);
                        }
                    }
                } else {
                    sendHelp(player);
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "This is a free for all game.");
        }
    }

    @Override
    public String getDescription() {
        return "It gives you teams information";
    }

    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------------------");
        player.sendMessage(ChatColor.translateAlternateColorCodes("&c/team create &7- Creates a team"));
        player.sendMessage(ChatColor.translateAlternateColorCodes("&c/team invite <player> &7- Invites a player"));
        player.sendMessage(ChatColor.translateAlternateColorCodes("&c/team kick <player> &7- Kicks a player"));
        player.sendMessage(ChatColor.translateAlternateColorCodes("&c/team join <player> &7- Joins a team"));
        player.sendMessage(ChatColor.translateAlternateColorCodes("&c/team leave &7- Leaves your team"));
        player.sendMessage(ChatColor.translateAlternateColorCodes("&c/team disband &7- Disbands your team"));
        player.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "---------------------------------");
    }
}
