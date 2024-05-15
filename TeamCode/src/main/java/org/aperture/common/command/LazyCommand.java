package org.aperture.common.command;

public class LazyCommand extends Command{
    public interface Lambda {
        Command get();
    }

    String descriptor;
    Lambda fn;
    Command command;
    boolean poll;

    public LazyCommand(String descriptor, Lambda lazyFunction, boolean poll) {
        this.descriptor = descriptor;
        customScheduler = new CommandScheduler(descriptor);
        this.fn = lazyFunction;
        this.poll = poll;
    }
    @Override
    public void init() {
        command = fn.get();
        customScheduler.runCommand(command);
    }

    @Override
    public boolean run() {
        if(poll) {
            Command newCommand = fn.get();
            if(command != newCommand) {
                command = newCommand;
                customScheduler.stopAll();
                customScheduler.runCommand(command);
            }
        }
        customScheduler.run();
        return customScheduler.activeCommands.isEmpty();
    }

    @Override
    public String getDescriptor() {
        return descriptor;
    }
}
