package org.aperture.common.command;

import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;
import java.util.List;

public abstract class Command {
    protected List<Command> children = new ArrayList<>();
    protected Command parent = null;
    protected boolean active = false;

    protected CommandScheduler customScheduler;
    protected ElapsedTime timeoutTimer = null;
    protected double timeoutMS = 0;
    public Command setTimeout(double ms) {
        this.timeoutMS = ms;
        timeoutTimer = new ElapsedTime();
        return this;
    }

    public Command then(String descriptor, Runnable fn) { return then(new LambdaCommand(descriptor, fn)); }
    public Command then(String descriptor, LambdaCommand.Lambda fn) { return then(new LambdaCommand(descriptor, fn)); }
    public final Command then(Command next) {
        children.add(next);
        next.parent = this;
        return next;
    }

    public Command branch(String descriptor, Runnable fn) { return branch(new LambdaCommand(descriptor, fn)); }
    public Command branch(String descriptor, LambdaCommand.Lambda fn) { return branch(new LambdaCommand(descriptor, fn)); }
    public final Command branch(Command next) {
        children.add(next);
        next.parent = this;
        return this;
    }

    public abstract void init();
    public abstract boolean run();
    public abstract String getDescriptor();
    public void cancelIfRunning() { active=false; }

    public boolean deepChildrenHaveFinished() {
        if(this.active) return false;
        boolean active = false;
        for(Command child:children) {
            if(!child.deepChildrenHaveFinished()) {
                active=true;
                break;
            }
        }
        return !active;

    }
    public static Command box(Command command) {
        Command parent = command;
        while (parent.parent!=null) {
            parent = parent.parent;
        }

        return parent;
    }
}
