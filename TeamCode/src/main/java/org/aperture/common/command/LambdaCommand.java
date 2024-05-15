package org.aperture.common.command;

public class LambdaCommand extends Command {

    public interface Lambda {
        boolean fn();
    }

    final String descriptor;
    private final Lambda lambda;

    public LambdaCommand(String descriptor, Runnable fn) { this(descriptor, () -> { fn.run(); return true; }); }
    public LambdaCommand(String descriptor, Lambda run) {
        this.descriptor =descriptor;
        lambda = run;
    }

    @Override
    public void init() {}

    @Override
    public String getDescriptor() { return descriptor; }

    @Override
    public boolean run() {
        return lambda.fn();
    }
}
