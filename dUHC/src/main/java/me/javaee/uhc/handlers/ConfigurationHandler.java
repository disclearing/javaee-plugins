package me.javaee.uhc.handlers;

import com.bizarrealex.aether.Aether;
import com.google.common.base.Strings;
import me.javaee.uhc.UHC;
import me.javaee.uhc.team.UHCTeam;
import me.javaee.uhc.utils.Configurator;
import me.javaee.uhc.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ConfigurationHandler {
    private static final String STRAIGHT_LINE_TEMPLATE = ChatColor.STRIKETHROUGH.toString() + Strings.repeat("-", 256);
    public static String STRAIGHT_LINE_DEFAULT = STRAIGHT_LINE_TEMPLATE.substring(0, 55);

    public static void sendConfigsToPlayer(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.translateAlternateColorCodes("&6&lUHC ACTUAL CONFIG:"));
        sender.sendMessage("");

        int counter = 0;
        for (String line : UHC.getInstance().getConfigurator().getOptionValues()) {
            sender.sendMessage(ChatColor.YELLOW.toString() + counter + ". " + ChatColor.GREEN + line);
            counter++;
        }
    }

    public static void handleConfigCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            ConfigurationHandler.usage(sender);
            return;
        }

        // Still just list options out
        if (args[0].equalsIgnoreCase("list")) {
            ConfigurationHandler.sendConfigsToPlayer(sender);
        } else if (args.length == 2) {
            // Try to update the config option value

            if (args[0].equalsIgnoreCase("UHCNUMBER")) {
                UHC.getInstance().setUhcNumber(args[1]);
                for (Player player : Bukkit.getOnlinePlayers()) {
                   player.getScoreboard().getObjective("glaedr_is_shit").setDisplayName(ChatColor.translateAlternateColorCodes("&6&lSilex &c[UHC #" + UHC.getInstance().getUhcNumber() + "]"));
                }
                sender.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have changed the scoreboard title."));
                return;
            }

            Object result;
            try {
                result = UHC.getInstance().getConfigurator().updateOption(args[0].toLowerCase(), args[1]);
            } catch (Configurator.NoSuchKeyExistsException e) {
                sender.sendMessage(ChatColor.RED + "Invalid config option.");
                return;
            }

            // Update current scoreboards

            if (result == null) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &6") + ChatColor.stripColor(UHC.getInstance().getConfigurator().getOption(args[0].toLowerCase()).getNiceName()) + " has been set to " + args[1] + ".");

                if (args[0].equalsIgnoreCase("PVPTIME")) {
                    Scenario rush = new Scenario("Rush", "Rush", new ItemBuilder(Material.POTION).setDurability(8194).build(), "PvPTime: " + UHC.getInstance().getConfigurator().getIntegerOption("PVPTIME").getValue() + "m, HealTime: " + UHC.getInstance().getConfigurator().getIntegerOption("HEALTIME").getValue() + "m, Border: 20m");

                    boolean wasEnabled = false;

                    if (Scenario.getByName("Rush").isEnabled()) {
                        Scenario.getByName("Rush").setEnabled(false);
                        wasEnabled = true;
                    }

                    UHC.getInstance().getScenarios().remove(Scenario.getByName("Rush"));
                    UHC.getInstance().getScenarios().add(rush);

                    if (wasEnabled) {
                        Scenario.getByName("Rush").setEnabled(true);
                    }
                }

                if (args[0].equalsIgnoreCase("TEAMSIZE") && args[1].equalsIgnoreCase("50")) {
                    UHCTeam red = new UHCTeam("Red", "Red");
                    UHCTeam blue = new UHCTeam("Blue", "Blue");

                    red.setNumber(1);
                    blue.setNumber(2);

                    UHC.getInstance().getTeams().add(red);
                    UHC.getInstance().getTeams().add(blue);
                }

                if (args[0].equalsIgnoreCase("HEALTIME")) {
                    Scenario rush = new Scenario("Rush", "Rush", new ItemBuilder(Material.POTION).setDurability(8194).build(), "PvPTime: " + UHC.getInstance().getConfigurator().getIntegerOption("PVPTIME").getValue() + "m, HealTime: " + UHC.getInstance().getConfigurator().getIntegerOption("HEALTIME").getValue() + "m, Border: 20m");

                    boolean wasEnabled = false;

                    if (Scenario.getByName("Rush").isEnabled()) {
                        Scenario.getByName("Rush").setEnabled(false);
                        wasEnabled = true;
                    }

                    UHC.getInstance().getScenarios().remove(Scenario.getByName("Rush"));
                    UHC.getInstance().getScenarios().add(rush);

                    if (wasEnabled) {
                        Scenario.getByName("Rush").setEnabled(true);
                    }
                }

                if (args[0].equalsIgnoreCase("LUCKYLEAVES")) {
                    Scenario luckyLeaves = new Scenario("Lucky Leaves", "LL", new ItemStack(Material.GOLDEN_APPLE), "Trees drops golden apples with " + UHC.getInstance().getConfigurator().getIntegerOption("LUCKYLEAVES").getValue() + "%!");

                    boolean wasEnabled = false;

                    if (Scenario.getByName("Lucky Leaves").isEnabled()) {
                        Scenario.getByName("Lucky Leaves").setEnabled(false);
                        wasEnabled = true;
                    }

                    UHC.getInstance().getScenarios().remove(Scenario.getByName("Lucky Leaves"));
                    UHC.getInstance().getScenarios().add(luckyLeaves);

                    if (wasEnabled) {
                        Scenario.getByName("Lucky Leaves").setEnabled(true);
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + result.toString());
                sender.sendMessage("------------------------");
                ConfigurationHandler.usage(sender);
            }
        } else {
            ConfigurationHandler.usage(sender);
        }
    }

    public static void usage(CommandSender sender) {
        sender.sendMessage("Usage:");
        sender.sendMessage("/uhc config list - Lists all flags and their values");
        sender.sendMessage("/uhc config <flag> <value> - Sets the specified flag to the given value");
    }

}