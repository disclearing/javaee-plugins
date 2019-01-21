package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.enums.GameState;
import me.javaee.uhc.team.UHCTeam;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class RuletaCommand extends BaseCommand {
    public RuletaCommand() {
        super("ruleta", Arrays.asList("randompvp", "pvprandom"), true, true);
    }

    List<Player> ruleteros = new ArrayList<>();

    ArrayList<Player> players = new ArrayList<>();
    ArrayList<UHCTeam> teams = new ArrayList<>();

    private int counter = 4;

    private String hasPvped1;
    private String hasPvped2;

    private UHCTeam hasTeam1;
    private UHCTeam hasTeam2;

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (UHC.getInstance().getGameManager().getGameState() == GameState.INGAME) {
            if (UHC.getInstance().getGameManager().getHost() == player) {
                if (args.length == 0) {
                    if (UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue() <= 1) {
                        if (UHC.getInstance().getGameManager().getAlivePlayers().size() < 3) {
                            player.sendMessage(ChatColor.RED + "There are less than 3 players alive.");
                            return;
                        }
                    } else {
                        if (UHC.getInstance().getTeams().size() < 3) {
                            player.sendMessage(ChatColor.RED + "There are less than 3 teams.");
                            return;
                        }
                    }

                    if (UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue() <= 1) {
                        new BukkitRunnable() {
                            public void run() {
                                if (counter == 3) {
                                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &eDeterminating fights in &63 seconds&e."));
                                } else if (counter == 2) {
                                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &eDeterminating fights in &62 seconds&e."));
                                } else if (counter == 1) {
                                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &eDeterminating fights in &61 second&e."));
                                } else if (counter <= 0) {
                                    for (UUID uuid : UHC.getInstance().getGameManager().getAlivePlayers()) {
                                        if (Bukkit.getPlayer(uuid) != null) {
                                            String name = Bukkit.getPlayer(uuid).getName();

                                            if (name.equalsIgnoreCase(hasPvped1) || name.equalsIgnoreCase(hasPvped2))
                                                continue;

                                            if (UHC.getInstance().getTimerManager().getCombatTagTimer().getRemaining(Bukkit.getPlayer(uuid)) > 0)
                                                continue;

                                            if (ScreenshareCommand.screenShared.contains(Bukkit.getPlayer(uuid).getUniqueId()))
                                                continue;

                                            players.add(Bukkit.getPlayer(uuid));
                                        }
                                    }

                                    Player pvp1 = players.get(new Random().nextInt(players.size()));
                                    hasPvped1 = pvp1.getName();
                                    players.remove(pvp1);

                                    Player pvp2 = players.get(new Random().nextInt(players.size()));
                                    hasPvped2 = pvp2.getName();
                                    players.remove(pvp2);

                                    Bukkit.broadcastMessage("");
                                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &6" + pvp1.getName() + " &eversus &6" + pvp2.getName()));
                                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&cYou have 1 minute to pvp, if you don't you will get banned permanently for trucing. If you clean you will get suspended from the uhc."));
                                    Bukkit.broadcastMessage("");

                                    players.clear();

                                    cancel();

                                    counter = 4;
                                }

                                counter--;
                            }
                        }.runTaskTimer(UHC.getInstance(), 0L, 20L);
                    } else {
                        new BukkitRunnable() {
                            public void run() {
                                if (counter == 3) {
                                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &eDeterminating fights in &63 seconds&e."));
                                } else if (counter == 2) {
                                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &eDeterminating fights in &62 seconds&e."));
                                } else if (counter == 1) {
                                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &eDeterminating fights in &61 second&e."));
                                } else if (counter <= 0) {
                                    for (UHCTeam team : UHC.getInstance().getTeams()) {
                                        if (team == hasTeam1 || team == hasTeam2) continue;

                                        teams.add(team);
                                    }

                                    UHCTeam team1 = teams.get(new Random().nextInt(teams.size()));
                                    hasTeam1 = team1;
                                    teams.remove(team1);

                                    UHCTeam team2 = teams.get(new Random().nextInt(teams.size()));
                                    hasTeam2 = team2;
                                    teams.remove(team2);

                                    ArrayList<String> sTeam1 = new ArrayList<>();
                                    for (UUID a : team1.getPlayerList()) {
                                        sTeam1.add(Bukkit.getOfflinePlayer(a).getName());
                                    }

                                    ArrayList<String> sTeam2 = new ArrayList<>();
                                    for (UUID a : team2.getPlayerList()) {
                                        sTeam2.add(Bukkit.getOfflinePlayer(a).getName());
                                    }

                                    Bukkit.broadcastMessage("");
                                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &6" + StringUtils.join(sTeam1, ", ") + " &eversus &6" + StringUtils.join(sTeam2, ", ")));
                                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&cYou have 1 minute to pvp, if you don't you will get banned permanently for trucing. If you clean you will get suspended from the uhc."));
                                    Bukkit.broadcastMessage("");

                                    sTeam1.clear();
                                    sTeam2.clear();

                                    teams.clear();

                                    cancel();
                                    counter = 4;
                                }

                                counter--;
                            }
                        }.runTaskTimer(UHC.getInstance(), 0L, 20L);
                    }
                } else if (args.length == 2) {
                    Player target1 = Bukkit.getPlayer(args[0]);
                    Player target2 = Bukkit.getPlayer(args[1]);

                    if (target1 == null || target2 == null) {
                        player.sendMessage(ChatColor.RED + "One of the ruleta's player is offline.");
                    } else {
                        if (UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue() <= 1) {
                            new BukkitRunnable() {
                                public void run() {
                                    if (counter == 3) {
                                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &eDeterminating fights in &63 seconds&e."));
                                    } else if (counter == 2) {
                                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &eDeterminating fights in &62 seconds&e."));
                                    } else if (counter == 1) {
                                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &eDeterminating fights in &61 second&e."));
                                    } else if (counter <= 0) {
                                        Bukkit.broadcastMessage("");
                                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &6" + target1.getName() + " &eversus &6" + target2.getName()));
                                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&cYou have 1 minute to pvp, if you don't you will get banned permanently for trucing. If you clean you will get suspended from the uhc."));
                                        Bukkit.broadcastMessage("");

                                        cancel();
                                        counter = 4;
                                    }

                                    counter--;
                                }
                            }.runTaskTimer(UHC.getInstance(), 0L, 20L);
                        } else {
                            new BukkitRunnable() {
                                public void run() {
                                    if (counter == 3) {
                                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &eDeterminating fights in &63 seconds&e."));
                                    } else if (counter == 2) {
                                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &eDeterminating fights in &62 seconds&e."));
                                    } else if (counter == 1) {
                                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &eDeterminating fights in &61 second&e."));
                                    } else if (counter <= 0) {
                                        UHCTeam team1 = UHCTeam.getByUUID(target1.getUniqueId());
                                        UHCTeam team2 = UHCTeam.getByUUID(target2.getUniqueId());

                                        ArrayList<String> sTeam1 = new ArrayList<>();
                                        for (UUID a : team1.getPlayerList()) {
                                            sTeam1.add(Bukkit.getOfflinePlayer(a).getName());
                                        }

                                        ArrayList<String> sTeam2 = new ArrayList<>();
                                        for (UUID a : team2.getPlayerList()) {
                                            sTeam2.add(Bukkit.getOfflinePlayer(a).getName());
                                        }

                                        Bukkit.broadcastMessage("");
                                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &6" + StringUtils.join(sTeam1, ", ") + " &eversus &6" + StringUtils.join(sTeam2, ", ")));
                                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&cYou have 1 minute to pvp, if you don't you will get banned permanently for trucing. If you clean you will get suspended from the uhc."));
                                        Bukkit.broadcastMessage("");

                                        sTeam1.clear();
                                        sTeam2.clear();

                                        teams.clear();

                                        cancel();
                                        counter = 4;
                                    }

                                    counter--;
                                }
                            }.runTaskTimer(UHC.getInstance(), 0L, 20L);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getDescription() {
        return "It choose 2 different players to pvp";
    }
}
