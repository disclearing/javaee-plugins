package me.redis.practice.duel;

import me.redis.practice.duel.argument.DuelAcceptArgument;
import me.redis.practice.duel.argument.DuelPlayerArgument;
import me.redis.practice.utils.command.ExecutableCommand;

public class DuelCommand extends ExecutableCommand {
    public DuelCommand() {
        super("duel");

        addArgument(new DuelAcceptArgument());
        addArgument(new DuelPlayerArgument());
    }
}