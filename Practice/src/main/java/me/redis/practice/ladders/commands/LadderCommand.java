package me.redis.practice.ladders.commands;

import me.redis.practice.ladders.commands.arguments.*;
import me.redis.practice.utils.command.ExecutableCommand;

public class LadderCommand extends ExecutableCommand {
    public LadderCommand() {
        super("ladder");

        addArgument(new CreateLadderArgument());
        addArgument(new DeleteLadderArgument());
        addArgument(new SetLadderIconArgument());
        addArgument(new SetLadderInventoryArgument());
        addArgument(new ListLadderArgument());
        addArgument(new SetRankedLadderArgument());
        addArgument(new SetLadderPositionArgument());
    }
}
