package org.aperture.common.command;

import java.util.Arrays;
import java.util.List;

public class CompositeCommand extends Command {
    List<Command> commands;
    final String descriptor;
    @SafeVarargs
    public CompositeCommand(String descriptor, Command... commands) {
        this.descriptor = descriptor;
        this.commands = Arrays.asList(commands);
        customScheduler = new CommandScheduler(descriptor);
    }

    @Override
    public String getDescriptor() { return descriptor; }


    @Override
    public void init() {
        for (Command command : commands) {
            customScheduler.runCommand(command);
        }
    }

    @Override
    public boolean run() {
        customScheduler.run();
        return customScheduler.activeCommands.isEmpty();
    }
}
