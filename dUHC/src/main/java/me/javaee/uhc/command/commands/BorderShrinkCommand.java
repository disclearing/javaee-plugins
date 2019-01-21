package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.enums.GameState;
import me.javaee.uhc.tasks.BorderShrinkTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class BorderShrinkCommand extends BaseCommand {

    public BorderShrinkCommand() {
        super("bordershrink", Arrays.asList("bs", "borders"), true, false);
    }

    public void helpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "----------------------------");
        sender.sendMessage(ChatColor.GOLD + "/bs {start} {interval} {amount} {minimum}");
        sender.sendMessage(ChatColor.GOLD + "Example: /bs 45 5 500 100");
        sender.sendMessage(ChatColor.GOLD + "Empieza a los 45 minutos, de 500 bloques en 500 bloques cada 5 minutos hasta llegar a 100");
        sender.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "----------------------------");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp() || ((Player) sender) == UHC.getInstance().getGameManager().getHost()) {
            if (UHC.getInstance().getGameManager().getGameState() != GameState.INGAME) {
                if (UHC.getInstance().getConfigurator().getOption(UHC.CONFIG_OPTIONS.RADIUS.name()).getValue() == null) {
                    sender.sendMessage(ChatColor.RED + "Border radius must be set in config first before turning on border shrinking.");
                    return;
                }

                if (args.length >= 4) {
                    try {
                        int startTime = Integer.valueOf(args[0]);
                        int shrinkInterval = Integer.valueOf(args[1]);
                        int shrinkAmount = Integer.valueOf(args[2]);
                        int minimumRadius = Integer.valueOf(args[3]);
                        int extraShrinkTime = -1;
                        int extraShrinkTime2 = -1;

                        if (args.length >= 5) {
                            extraShrinkTime = Integer.valueOf(args[4]);
                        }

                        if (args.length == 6) {
                            extraShrinkTime2 = Integer.valueOf(args[5]);
                        }

                        // Check for bad numbers
                        if (startTime < 0 || startTime > 270) {
                            sender.sendMessage(ChatColor.RED + "Start time must be between 1 minute and 270 minutes");
                            return;
                        }
                        if (shrinkInterval < 1 || shrinkInterval > 60) {
                            sender.sendMessage(ChatColor.RED + "Shrink interval must be between 1 minute and 60 minutes");
                            return;
                        }
                        if (shrinkAmount < 10 || shrinkInterval > 500) {
                            sender.sendMessage(ChatColor.RED + "Shrink amount must be between 10 blocks and 500 blocks");
                            return;
                        }

                        if (minimumRadius < 25 || minimumRadius > (int) UHC.getInstance().getConfigurator().getOption(UHC.CONFIG_OPTIONS.RADIUS.name()).getValue()) {
                            sender.sendMessage(ChatColor.RED + "The minimum border radius must be between 100 blocks and the starting radius (" + UHC.getInstance().getConfigurator().getOption(UHC.CONFIG_OPTIONS.RADIUS.name()).getValue() + " blocks)");
                            return;
                        }

                        UHC.getInstance().setBorderShrinkTask(new BorderShrinkTask(startTime, shrinkInterval, shrinkAmount, minimumRadius, extraShrinkTime, extraShrinkTime2));
                        sender.sendMessage(ChatColor.GREEN + "Empieza a los " + startTime + ", de " + shrinkAmount + " bloques cada " + shrinkInterval + " minutos hasta " + minimumRadius + "x" + minimumRadius);

                    } catch (NumberFormatException e) {
                        this.helpMessage(sender);
                    }
                } else {
                    this.helpMessage(sender);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You cannot use that command after the UHC has started");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
        }
    }

    @Override
    public String getDescription() {
        return "Set the shrinking time";
    }
}
