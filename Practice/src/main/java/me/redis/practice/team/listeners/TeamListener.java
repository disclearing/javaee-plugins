package me.redis.practice.team.listeners;

import me.redis.practice.Practice;
import me.redis.practice.enums.MatchType;
import me.redis.practice.enums.TeamMatchType;
import me.redis.practice.enums.TeamStatus;
import me.redis.practice.events.*;
import me.redis.practice.ladders.Ladder;
import me.redis.practice.match.type.FreeForAllMatch;
import me.redis.practice.match.type.TeamMatch;
import me.redis.practice.profile.Profile;
import me.redis.practice.team.Team;
import me.redis.practice.utils.ItemBuilder;
import me.redis.practice.utils.PracticeUtils;
import me.redis.practice.utils.SerializationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TeamListener implements Listener {
    private static Map<UUID, TeamMatchType> selectedEvent = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

        if (profile.getTeam() == null) {
            return;
        }

        if (profile.getTeam().getLeaderUuid().equals(player.getUniqueId())) {
            PlayerDisbandTeamEvent TeamEvent = new PlayerDisbandTeamEvent(player, profile.getTeam());
            Bukkit.getPluginManager().callEvent(TeamEvent);
        } else {
            PlayerLeaveTeamEvent TeamEvent = new PlayerLeaveTeamEvent(player, profile.getTeam(), false, true);
            Bukkit.getPluginManager().callEvent(TeamEvent);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

        if (profile.getTeam() == null) {
            return;
        }

        if (profile.getTeam().getLeaderUuid().equals(player.getUniqueId())) {
            PlayerDisbandTeamEvent TeamEvent = new PlayerDisbandTeamEvent(player, profile.getTeam());
            Bukkit.getPluginManager().callEvent(TeamEvent);
        } else {
            PlayerLeaveTeamEvent TeamEvent = new PlayerLeaveTeamEvent(player, profile.getTeam(), false, true);
            Bukkit.getPluginManager().callEvent(TeamEvent);
        }
    }

    @EventHandler
    public void onCreateTeam(PlayerCreateTeamEvent event) {
        Player player = event.getPlayer();
        Practice.getPlugin().getTeamManager().addTeam(event.getTeam());

        PracticeUtils.resetPlayer(player);

        player.getInventory().setContents(PracticeUtils.getTeamLeaderInventory());
        player.updateInventory();
        player.sendMessage(ChatColor.BLUE + "You have created a new team.");
    }

    @EventHandler
    public void onDisbandTeam(PlayerDisbandTeamEvent event) {
        Team Team = event.getTeam();

        Practice.getPlugin().getTeamManager().removeTeam(Team);
        Team.sendMessage(ChatColor.BLUE + "The team has been disbanded.");

        List<Player> players = Team.getMembers();

        for (Player player : players) {
            Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId()).setTeam(null);

            PracticeUtils.resetPlayer(player);

            player.getInventory().setContents(PracticeUtils.getLobbyInventory());
            player.updateInventory();
        }

        event.getPlayer().sendMessage(ChatColor.BLUE + "You have disbanded the team.");
    }

    @EventHandler
    public void onJoinTeam(PlayerJoinTeamEvent event) {
        Team Team = event.getTeam();
        Player player = event.getPlayer();
        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

        profile.setTeam(Team);

        PracticeUtils.resetPlayer(player);

        player.getInventory().setContents(PracticeUtils.getTeamMemberInventory());
        player.updateInventory();

        Team.removeInvite(player.getUniqueId());
        Team.addPlayer(player);
        Team.sendMessage(ChatColor.BLUE + event.getPlayer().getName() + " has joined the team.");
    }

    @EventHandler
    public void onLeaveTeam(PlayerLeaveTeamEvent event) {
        Team Team = event.getTeam();
        Player player = event.getPlayer();
        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

        if (Team.getStatus() == TeamStatus.QUEUING) {
            profile.getQueueData().getQueue().removeFromQueue(profile.getQueueData());
        }

        profile.setTeam(null);
        Team.removePlayer(player);

        if (event.shouldClean()) {
            PracticeUtils.resetPlayer(player);

            player.getInventory().setContents(PracticeUtils.getLobbyInventory());
            player.updateInventory();
        }

        if (event.shouldAnnounce()) {
            Team.sendMessage(ChatColor.BLUE + player.getName() + " has left the team.");
        }
    }

    @EventHandler
    public void onKickPlayer(PlayerKickTeamEvent event) {
        Team Team = event.getTeam();
        Player player = event.getPlayer();
        Player kicked = event.getKickedPlayer();

        Practice.getPlugin().getProfileManager().getProfile(kicked.getUniqueId()).setTeam(null);

        Team.removePlayer(kicked);

        if (event.shouldClean()) {
            PracticeUtils.resetPlayer(kicked);
            kicked.getInventory().setContents(PracticeUtils.getLobbyInventory());
            kicked.updateInventory();
        }

        if (event.shouldAnnounce()) {
            Team.sendMessage(ChatColor.BLUE + kicked.getName() + " has been kicked from the team by " + player.getName() + ".");
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getItem() != null && event.getItem().getItemMeta() != null && event.getItem().getItemMeta().getDisplayName() != null) {
            ItemStack item = event.getItem();
            Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());
            Team team = profile.getTeam();

            if (team != null) {
                if (item.getType() == Material.SKULL_ITEM) {
                    player.performCommand("team info");
                } else if (item.getType() == Material.FIRE) {
                    player.performCommand("team disband");
                } else if (item.getItemMeta().getDisplayName().contains("events")) {
                    if (team.getMembers().size() < 2) {
                        player.sendMessage(ChatColor.RED + "You must have more than 2 players in your party to start an event.");
                        return;
                    }

                    Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.BLUE + "Select an event");
                    inventory.setItem(2, new ItemBuilder(Material.EYE_OF_ENDER).setDisplayName(ChatColor.BLUE + "Team Deathmatch").create());
                    inventory.setItem(6, new ItemBuilder(Material.GLOWSTONE_DUST).setDisplayName(ChatColor.BLUE + "Free for All").create());

                    player.openInventory(inventory);
                }
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player);

        if (event.getClickedInventory() != null && event.getCurrentItem() != null && event.getClickedInventory().getName() != null) {
            if (event.getCurrentItem().getItemMeta() != null && event.getCurrentItem().getItemMeta().getDisplayName() != null) {
                if (event.getClickedInventory().getName().equalsIgnoreCase(ChatColor.BLUE + "Select an event")) {
                    if (event.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().contains("free for all")) {
                        selectedEvent.put(player.getUniqueId(), TeamMatchType.FREE_FOR_ALL);

                        player.closeInventory();

                        Inventory newInv = Bukkit.createInventory(null, 9, ChatColor.BLUE + "Select a ladder...");

                        if (!Practice.getPlugin().getLadderManager().getLadders().isEmpty()) {
                            for (Ladder ladder : Practice.getPlugin().getLadderManager().getLadders().values()) {
                                if (!ladder.getName().toLowerCase().equalsIgnoreCase("sumo")) {
                                    newInv.addItem(new ItemBuilder(SerializationUtils.itemStackFromString(ladder.getIcon())).setDisplayName(ChatColor.GREEN + ladder.getName()).create());
                                }
                            }
                        }

                        player.openInventory(newInv);
                    } else if (event.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().contains("team deathmatch")) {
                        selectedEvent.put(player.getUniqueId(), TeamMatchType.TEAM_DEATHMATCH);

                        player.closeInventory();

                        Inventory newInv = Bukkit.createInventory(null, 9, ChatColor.BLUE + "Select a ladder...");

                        if (!Practice.getPlugin().getLadderManager().getLadders().isEmpty()) {
                            for (Ladder ladder : Practice.getPlugin().getLadderManager().getLadders().values()) {
                                if (!ladder.getName().toLowerCase().equalsIgnoreCase("sumo")) {
                                    newInv.addItem(new ItemBuilder(SerializationUtils.itemStackFromString(ladder.getIcon())).setDisplayName(ChatColor.GREEN + ladder.getName()).create());
                                }
                            }
                        }

                        player.openInventory(newInv);
                    }

                    event.setCancelled(true);
                } else if (event.getClickedInventory().getName().equalsIgnoreCase(ChatColor.BLUE + "Select a ladder...")) {
                    if (profile.getTeam() == null) {
                        player.sendMessage(ChatColor.RED + "You must be in a team.");
                        player.closeInventory();
                    }

                    if (profile.getTeam().getMembers().size() < 2) {
                        player.sendMessage(ChatColor.RED + "You must have more than 2 players in your team to start an event.");
                        player.closeInventory();
                        event.setCancelled(true);
                    }

                    Ladder ladder = Practice.getPlugin().getLadderManager().getLadders().get(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));

                    if (ladder == null) {
                        player.sendMessage(ChatColor.RED + "Ladder does not exist.");
                        return;
                    }

                    if (!selectedEvent.containsKey(player.getUniqueId())) {
                        player.sendMessage(ChatColor.RED + "You haven't selected any event.");
                        return;
                    }

                    switch (selectedEvent.get(player.getUniqueId())) {
                        case TEAM_DEATHMATCH:
                            List<Player> team1 = new ArrayList<>();
                            List<Player> team2 = new ArrayList<>();

                            for (Player p : profile.getTeam().getMembers()) {
                                if (team1.size() > team2.size()) {
                                    team2.add(p);
                                } else if (team2.size() > team1.size()) {
                                    team1.add(p);
                                } else {
                                    Random r = new Random();

                                    if (r.nextBoolean()) {
                                        team1.add(p);
                                    } else {
                                        team2.add(p);
                                    }
                                }
                            }

                            TeamMatch teamMatch = new TeamMatch(null, ladder, Practice.getPlugin().getArenaManager().getRandomArena(), MatchType.TEAM_VS_TEAM, team1, team2);
                            Practice.getPlugin().getMatchManager().getMatches().put(teamMatch.getIdentifier(), teamMatch);
                            break;
                        case FREE_FOR_ALL:
                            FreeForAllMatch ffaMatch = new FreeForAllMatch(ladder, Practice.getPlugin().getArenaManager().getRandomArena(), profile.getTeam().getMembers());
                            Practice.getPlugin().getMatchManager().getMatches().put(ffaMatch.getUniqueId(), ffaMatch);
                            player.closeInventory();
                            break;
                        default:
                            player.sendMessage(ChatColor.RED + "Team does not exist.");
                            player.closeInventory();
                    }

                    event.setCancelled(true);
                }
            }
        }
    }
}