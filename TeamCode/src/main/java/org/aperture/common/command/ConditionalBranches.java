package org.aperture.common.command;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/*
new ConditionalBranches(
condition, command
condition, command,
condition, command
 */

public class ConditionalBranches extends Command {
    private final String descriptor;
    List<Command> commands;
    List<Callable<Boolean>> conditions;
    List<Command> normalChildren;
    final boolean continuing;
    public ConditionalBranches(String descriptor, List<Callable<Boolean>> conditions, List<Command> commands, boolean continuing) {
        assert commands.size() == conditions.size();
        this.commands =commands;
        this.conditions = conditions;
        this.descriptor = descriptor;
        this.continuing = continuing;
    }

    @Override
    public void init() {
        if(normalChildren!=null) this.children = normalChildren;
    }

    @Override
    public boolean run() {
        for(int i=0; i<commands.size(); i++) {
            try {
                if(conditions.get(i).call()) {
                    if(commands.get(i)!=null) {
                        normalChildren = new ArrayList<>(this.children);
                        this.children.clear();
                        if(continuing) {
                            Command newCommand = new CompositeCommand("conditonalSandbox",commands.get(i));
                            this.children.add(newCommand);
                            newCommand.children = normalChildren;
                        } else {
                            this.children.add(commands.get(i));
                        }
                    }
                    return true;
                }
            } catch (Exception ignored) {}
        }
        return false;
    }

    @Override
    public String getDescriptor() {
        return descriptor;
    }
}
