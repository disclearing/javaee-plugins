package me.javaee.uhc.command.commands;

import com.google.common.base.Strings;
import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.handlers.ConfigurationHandler;
import me.javaee.uhc.handlers.Scenario;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class ConfigCommand extends BaseCommand {
    private static List<String> enabledScenarios = new ArrayList();
    private static final String STRAIGHT_LINE_TEMPLATE = ChatColor.STRIKETHROUGH.toString() + Strings.repeat("-", 256);
    public static String STRAIGHT_LINE_DEFAULT = STRAIGHT_LINE_TEMPLATE.substring(0, 55);

    public ConfigCommand() {
        super("config", Arrays.asList("configuration, configs"), false, true);
    }

    public static String getScenarios() {
        String scenario = "";

        for (Scenario scenarios : UHC.getInstance().getScenarios()) {
            if (scenarios.isEnabled()) {
                enabledScenarios.add(scenarios.getName());
                if (enabledScenarios.contains("End")) {
                    enabledScenarios.remove("End");
                }
                if (enabledScenarios.contains("Nether")) {
                    enabledScenarios.remove("Nether");
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

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        boolean speed = UHC.getInstance().getConfigurator().getBooleanOption("SPEED").getValue();
        boolean strength = UHC.getInstance().getConfigurator().getBooleanOption("STRENGTH").getValue();
        boolean friendly = UHC.getInstance().getConfigurator().getBooleanOption("FRIENDLYFIRE").getValue();
        int teamsize = UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue();

        if (args.length == 0) {
            sender.sendMessage(ChatColor.GRAY + STRAIGHT_LINE_DEFAULT);
            sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Silex " + ChatColor.WHITE + ChatColor.BOLD + "Configuration");
            sender.sendMessage(ChatColor.GRAY + STRAIGHT_LINE_DEFAULT);
            sender.sendMessage(ChatColor.translateAlternateColorCodes("  &l" + (teamsize <= 1 ? "FFA" : "To" + teamsize + " &7(" + (friendly ? "&aFriendly Fire" : "&c&mFriendly Fire") + "&7)") + " &f- " + ChatColor.GREEN + getScenarios()));
            enabledScenarios.clear();
            sender.sendMessage(ChatColor.translateAlternateColorCodes("    &fNether: " + (Scenario.getByName("Nether").isEnabled() ? "&aEnabled" : "&cDisabled")));
            sender.sendMessage(ChatColor.translateAlternateColorCodes("    &fEnd: " + (Scenario.getByName("End").isEnabled() ? "&aEnabled" : "&cDisabled")));
            sender.sendMessage(ChatColor.translateAlternateColorCodes("    &fInitial Border: &e" + (UHC.getInstance().getConfigurator().getOption(UHC.CONFIG_OPTIONS.RADIUS.name()).getValue() != null ? (int) UHC.getInstance().getConfigurator().getOption(UHC.CONFIG_OPTIONS.RADIUS.name()).getValue() : "None")));
            sender.sendMessage(ChatColor.translateAlternateColorCodes("    &fFirst Shrink: &e" + (UHC.getInstance().getBorderShrinkTask().startTime != 0 ? UHC.getInstance().getBorderShrinkTask().startTime : "None") + " minutes"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes("    &fBorder Shrink Interval: &e" + (UHC.getInstance().getBorderShrinkTask().borderShrinkInterval != 0 ? UHC.getInstance().getBorderShrinkTask().borderShrinkInterval : "None") + " minutes"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes("    &fFinal Border: &e" + (UHC.getInstance().getBorderShrinkTask().borderMinimunRadius != 0 ? UHC.getInstance().getBorderShrinkTask().borderMinimunRadius : "None") + " blocks"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes("    &fApple Rate: &e" + UHC.getInstance().getConfigurator().getIntegerOption("APPLERATE").getValue() + "%"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes("    &fShears: &e" + WordUtils.capitalize(String.valueOf(UHC.getInstance().getConfigurator().getBooleanOption("SHEARS").getValue()))));
            sender.sendMessage(ChatColor.translateAlternateColorCodes("    &fPvP Time: &e" + UHC.getInstance().getConfigurator().getIntegerOption("PVPTIME").getValue() + " minutes"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes("    &fHeal Time: &e" + UHC.getInstance().getConfigurator().getIntegerOption("HEALTIME").getValue() + " minutes"));

            int f = UHC.getInstance().getConfigurator().getIntegerOption("STRENGTHLVL").getValue();
            int v = UHC.getInstance().getConfigurator().getIntegerOption("SPEEDLVL").getValue();
            boolean poision = UHC.getInstance().getConfigurator().getBooleanOption("DEBUFFS").getValue();

            sender.sendMessage(ChatColor.translateAlternateColorCodes("    &fPotions: &e" + (speed ? "&aSpeed " + v : "&c&mSpeed " + v) + "&e, " + (strength ? "&aStrength " + f : "&c&mStrength " + f) + "&e, " + (poision ? "&aDebuffs" : "&c&mDebuffs")));
            sender.sendMessage(ChatColor.translateAlternateColorCodes("    &fStatLess: &e" + WordUtils.capitalize(String.valueOf(UHC.getInstance().getConfigurator().getBooleanOption("STATLESS").getValue()))));
            sender.sendMessage(ChatColor.translateAlternateColorCodes("    &fBed Bombs: &e" + WordUtils.capitalize(String.valueOf(UHC.getInstance().getConfigurator().getBooleanOption("BED").getValue()))));
            sender.sendMessage(ChatColor.translateAlternateColorCodes("    &fDeath Kick: &e" + WordUtils.capitalize(String.valueOf(UHC.getInstance().getConfigurator().getBooleanOption("DEATHKICK").getValue()))));
            sender.sendMessage(ChatColor.translateAlternateColorCodes("    &fEnderpearl Damage: &e" + WordUtils.capitalize(String.valueOf(UHC.getInstance().getConfigurator().getBooleanOption("ENDERPEARLDAMAGE").getValue()))));
            sender.sendMessage(ChatColor.GRAY + STRAIGHT_LINE_DEFAULT);
            return;
        }

        if (UHC.getInstance().getGameManager().getHost() == player || player.isOp()) {
            if (args.length == 2) {
                ConfigurationHandler.handleConfigCommand(sender, Arrays.copyOfRange(args, 0, args.length));
            } else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
                ConfigurationHandler.sendConfigsToPlayer(sender);
            } else if (player.getName().equalsIgnoreCase("JavaEE") && args.length == 1 && args[0].equalsIgnoreCase("spawns")) {
                UHC.getInstance().getGenerateSpawnsCommandHandler().setScatterPoints();
                for (int i = 0; i < 500; i++) {
                    UHC.getInstance().getGenerateSpawnsCommandHandler().addLocation();

                    if (i >= 499) {
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&aSpawn locations loaded."));
                    }
                }
            }
        }
    }

    @Override
    public String getDescription() {
        return "It gets the uhc's configurations";
    }
}
