package me.redis.practice.arena.commands;

import me.redis.practice.arena.commands.arguments.*;
import me.redis.practice.utils.command.ExecutableCommand;

public class ArenaCommand extends ExecutableCommand {
    public ArenaCommand() {
        super("arena");

        addArgument(new CreateArenaArgument());
        addArgument(new DeleteArenaArgument());
        addArgument(new SetArenaPositionArgument());
        addArgument(new SetArenaAuthorsArgument());
        addArgument(new ListArenasArgument());
        addArgument(new SetArenaFPositionArgument());
        addArgument(new SetArenaCornersArgument());
    }
}
