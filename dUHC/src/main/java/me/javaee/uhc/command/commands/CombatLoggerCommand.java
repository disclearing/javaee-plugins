package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.combatlogger.CombatLogger;
import me.javaee.uhc.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CombatLoggerCommand extends BaseCommand {
    public CombatLoggerCommand() {
        super("combatlogger", Collections.singletonList("logger"), true, false);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <list/name>");
            return;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            sender.sendMessage(ChatColor.RED + "Current Combatloggers:");

            Bukkit.getWorlds().forEach(world -> {
                for (LivingEntity entity : world.getEntitiesByClass(Villager.class)) {
                    CombatLogger combatLogger = UHC.getInstance().getCombatLoggerManager().getByEntity(entity);

                    if (combatLogger != null) sender.sendMessage(ChatColor.translateAlternateColorCodes(" &7- &e" + combatLogger.getName()));
                }
            });
        } else if (args.length == 1 && !args[0].equalsIgnoreCase("list")) {
            String target = args[0];
            CombatLogger combatLogger = UHC.getInstance().getCombatLoggerManager().getByName(target);

            if (combatLogger != null) {
                UHC.getInstance().getCombatLoggerManager().removeCombatLogger(combatLogger.getEntity());
            } else {
                sender.sendMessage(ChatColor.RED + "That combatlogger does not exist (null).");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <list/name>");
        }
    }

    @Override
    public String getDescription() {
        return "It gives you a list of combatloggers that you can kill";
    }
}