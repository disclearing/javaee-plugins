package me.javaee.uhc.command.commands;

import com.google.gson.JsonObject;
import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.handlers.Scenario;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GameWhitelistCommand extends BaseCommand {
    private int counter = 300;

    public GameWhitelistCommand() {
        super("gamewhitelist", Arrays.asList("gwl"), true, true);
    }

    private int a = 0, b = 0;

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (UHC.getInstance().getUhcNumber().equalsIgnoreCase("1")) {
            sender.sendMessage(ChatColor.RED + "You need to set the uhc number.");
            return;
        }

        if (args.length == 1) {
            if (isInt(args[0])) {
                final int[] minutes = {Integer.parseInt(args[0])};
                int teams = UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue();

                if (a != 0) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have cancelled the first task."));
                    cancelTask(a);
                    a = 0;

                    if (b != 0) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have cancelled the second task."));
                        cancelTask(b);
                        b = 0;
                    }

                    sender.sendMessage(ChatColor.translateAlternateColorCodes("&cMake the '&7/gwl " + args[0] + "&c' again to start it."));
                    return;
                }

                a = Bukkit.getScheduler().scheduleSyncRepeatingTask(UHC.getInstance(), () -> {
                    if (minutes[0] < 1) {
                        enabledScenarios.clear();

                        b = Bukkit.getScheduler().scheduleSyncRepeatingTask(UHC.getInstance(), () -> {
                            if (counter == 299) {
                                enabledScenarios.clear();
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&a&lThe game will start in " + 5 + " minutes."));
                            } else if (counter == 240) {
                                enabledScenarios.clear();
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&a&lThe game will start in " + 4 + " minutes."));
                            } else if (counter == 180) {
                                enabledScenarios.clear();
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&a&lThe game will start in " + 3 + " minutes."));
                            } else if (counter == 120) {
                                enabledScenarios.clear();
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&a&lThe game will start in " + 2 + " minutes."));
                            } else if (counter == 60) {
                                enabledScenarios.clear();
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&a&lThe game will start in " + 1 + " minute."));
                            } else if (counter == 30) {
                                enabledScenarios.clear();
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&a&lThe game will start in " + 30 + " seconds."));

                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    player.getInventory().setContents(new ItemStack[0]);
                                    player.getInventory().setArmorContents(null);
                                    player.getInventory().clear();
                                    player.getInventory().setArmorContents(null);
                                    player.setHealth(20);
                                    player.setFoodLevel(20);

                                    player.removePotionEffect(PotionEffectType.ABSORPTION);
                                    player.teleport(Bukkit.getWorld("lobby").getSpawnLocation());
                                    UHC.getInstance().getPracticeManager().getPracticePlayers().remove(player);
                                    UHC.getInstance().getConfigurator().getBooleanOption("PRACTICE").setValue(false);
                                }
                            } else if (counter == 15) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "start");
                            } else if (counter == 10) {
                                for (int i = 0; i < 10; i++) {
                                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&eDo &lNOT &erelog until the game &c&lSTARTS&e."));
                                }

                                cancelTask(b);
                            }

                            counter--;
                        }, 0L, 20L);

                        cancelTask(a);
                    }

                    UHC.getInstance().setMinutes(minutes[0] + "");

                    JsonObject object = new JsonObject();
                    object.addProperty("server", Bukkit.getServerName());
                    object.addProperty("minutes", minutes[0]);

                    UHC.getInstance().getRedisMessagingHandler().sendMessage("uhc:whitelist", object.toString());
                    minutes[0]--;
                }, 0L, 20 * 60L);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Date date = new Date();
                        date.setTime(System.currentTimeMillis() + (60000 * minutes[0]));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have programmed this game to start at: &b" + date));

                        String scenarios = getScenarios();
                        if (Scenario.getByName("Rush").isEnabled() && (Scenario.getByName("Statless").isEnabled() || UHC.getInstance().getConfigurator().getBooleanOption("Statless").getValue())) {
                            scenarios += " (RUSH | STATSLESS)";
                        } else {
                            if (Scenario.getByName("Rush").isEnabled()) {
                                scenarios += " (RUSH)";
                            }

                            if (Scenario.getByName("Statless").isEnabled() || UHC.getInstance().getConfigurator().getBooleanOption("Statless").getValue()) {
                                scenarios += " (STATSLESS)";
                            }
                        }

                        String[] lines = {
                                "° UHC #" + UHC.getInstance().getUhcNumber() + " (SEASON: #2) | TEST",
                                "",
                                "· Teams: " + (UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue() < 2 ? "FFA" : "To" + UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue()),
                                "· Gamemodes: " + scenarios,
                                "· Time: " + date.getHours() + ":" + (String.valueOf(date.getMinutes()).length() == 1 ? "0" + date.getMinutes() : date.getMinutes()) + " (EST - " + (minutes[0] + 1) + "m)",
                                "· Slots: " + Bukkit.getMaxPlayers(),
                                "",
                                "· IP: silexpvp.net | " + Bukkit.getServerName() + " (1.7-1.8)",
                                "",
                                "◄ 50 ❤'s = 150 Slots ►"
                        };

                        String tweet = "";

                        for (String line : lines) {
                            tweet += "\n" + line;
                        }

                        try {
                            if (!Scenario.getByName("BuildUHC").isEnabled()) {
                                UHC.getInstance().getTwitters()[0].sendDirectMessage("UHCNR", tweet);
                            }

                            for (Twitter twitter : UHC.getInstance().getTwitters()) {
                                twitter.updateStatus(tweet);
                            }

                            Bukkit.broadcastMessage(ChatColor.GREEN + "Tweet have been successfully posted.");
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', " &7- &9" + UHC.getInstance().getTwitters()[0].getScreenName()));
                        } catch (TwitterException ex) {
                            ex.printStackTrace();
                            Bukkit.broadcastMessage(ChatColor.RED + "There was an error posting the tweet.");
                        }
                    }
                }.runTaskAsynchronously(UHC.getInstance());
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <minutes>");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <minutes>");
        }
    }

    @Override
    public String getDescription() {
        return "It starts the game automatically";
    }

    public boolean isInt(String number) {
        try {
            int i = Integer.parseInt(number);

            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private List<String> enabledScenarios = new ArrayList();

    public String getScenarios() {
        enabledScenarios.clear();

        String scenario = "";

        for (Scenario scenarios : UHC.getInstance().getScenarios()) {
            if (scenarios.isEnabled()) {
                enabledScenarios.add(scenarios.getName());
                if (enabledScenarios.contains("Statless")) {
                    enabledScenarios.remove("Statless");
                }
                if (enabledScenarios.contains("Rush")) {
                    enabledScenarios.remove("Rush");
                }
                scenario = StringUtils.join(enabledScenarios, ", ");
            }
        }

        if (scenario.equalsIgnoreCase("")) {
            return "None";
        } else {
            return scenario;
        }
    }

    public void cancelTask(int task) {
        Bukkit.getServer().getScheduler().cancelTask(task);
        task = 0;
        System.out.println(task);
    }
}
