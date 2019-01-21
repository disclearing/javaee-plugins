package me.redis.practice.team.commands;

import me.redis.practice.team.commands.arguments.*;
import me.redis.practice.utils.command.ExecutableCommand;

public class TeamCommand extends ExecutableCommand {
    public TeamCommand() {
        super("team");

        addArgument(new TeamCreateCommand());
        addArgument(new TeamDisbandCommand());
        addArgument(new TeamInfoCommand());
        addArgument(new TeamInviteCommand());
        addArgument(new TeamJoinCommand());
        addArgument(new TeamKickCommand());
        addArgument(new TeamLeaveCommand());
        addArgument(new TeamOpenCommand());
    }
}
