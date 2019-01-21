package me.redis.practice.utils.command;

import lombok.Getter;
import me.redis.practice.Practice;
import me.redis.practice.arena.commands.ArenaCommand;
import me.redis.practice.duel.DuelCommand;
import me.redis.practice.kit.commands.KitCommand;
import me.redis.practice.ladders.commands.LadderCommand;
import me.redis.practice.misc.commands.InventoryCommand;
import me.redis.practice.misc.commands.QueuesCommand;
import me.redis.practice.misc.commands.RestartCommand;
import me.redis.practice.misc.commands.StatisticsCommand;
import me.redis.practice.spectator.SpectateCommand;
import me.redis.practice.team.commands.TeamCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;

public class CommandRegistrer {
    private CommandMap commandMap;

    public CommandRegistrer() {
        registerCommand(new ArenaCommand(), Practice.getPlugin(), true);
        registerCommand(new LadderCommand(), Practice.getPlugin(), true);
        registerCommand(new DuelCommand(), Practice.getPlugin(), false);
        registerCommand(new InventoryCommand(), Practice.getPlugin(), false);
        registerCommand(new TeamCommand(), Practice.getPlugin(), false);
        registerCommand(new RestartCommand(), Practice.getPlugin(), true);
        registerCommand(new KitCommand(), Practice.getPlugin(), true);
        registerCommand(new StatisticsCommand(), Practice.getPlugin(), false);
        registerCommand(new SpectateCommand(), Practice.getPlugin(), false);
        registerCommand(new QueuesCommand(), Practice.getPlugin(), true);
    }

    public CommandMap getCommandMap() {
        if (commandMap != null) {
            return commandMap;
        }

        try {
            Field field = SimplePluginManager.class.getDeclaredField("commandMap");
            field.setAccessible(true);

            commandMap = (CommandMap) field.get(Bukkit.getPluginManager());
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return commandMap;
    }

    public PluginCommand getCommand(String name, Plugin owner) {
        PluginCommand command = null;

        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);

            command = constructor.newInstance(name, owner);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return command;
    }

    public void registerCommand(ExecutableCommand executableCommand, Plugin owner, boolean requiresPermission) {
        PluginCommand command = getCommand(executableCommand.getName(), owner);

        command.setPermissionMessage(ChatColor.RED + "You do not have permission to execute this command.");

        if (requiresPermission) {
            command.setPermission((owner.getName() + ".command." + executableCommand.getName()).toLowerCase());
        }

        if (executableCommand.getDescription() != null) {
            command.setDescription(executableCommand.getDescription());
        }

        command.setAliases(Arrays.asList(executableCommand.getAliases()));

        command.setExecutor(executableCommand);
        command.setTabCompleter(executableCommand);

        if (!getCommandMap().register(executableCommand.getName(), command)) {
            command.unregister(getCommandMap());
            getCommandMap().register(executableCommand.getName(), command);
        }
    }
}
