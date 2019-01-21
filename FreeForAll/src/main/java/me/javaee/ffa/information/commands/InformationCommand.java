package me.javaee.ffa.information.commands;

import me.javaee.ffa.information.commands.argments.*;
import me.javaee.ffa.utils.command.ExecutableCommand;

public class InformationCommand extends ExecutableCommand {
    public InformationCommand() {
        super("information", null, "info", "bunkers");

        addArgument(new SetSpawnArgument());
        addArgument(new SetKothAreaArgument());
        addArgument(new StartKothArgument());
        addArgument(new SetSpawnAreaArgument());
        addArgument(new SetRecordingArgument());
        addArgument(new StopKothArgument());
        addArgument(new AnnouncesArgument());
        addArgument(new ForceCheckLeaderboardsArgument());
    }
}
