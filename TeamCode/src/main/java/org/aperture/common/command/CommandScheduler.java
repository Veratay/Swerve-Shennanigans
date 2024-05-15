package org.aperture.common.command;

import org.aperture.LogConfig;

import java.util.ArrayList;
import java.util.List;

public class CommandScheduler {
    public List<Command> activeCommands;
    String name;
    String baseName;
    public CommandScheduler(String name) {
        this.name = this.baseName =  name;
        activeCommands = new ArrayList<>();
    }

    public void runCommand(Command command) {
        command.active = true;
        if(command.timeoutTimer!=null) command.timeoutTimer.reset();
        activeCommands.add(command);
        command.init();
    }

    public void run() {
        List<Command> processQueue = new ArrayList<>(activeCommands);

        StringBuilder commandsRunning = new StringBuilder();
        for(Command command : processQueue) {
            if(command.customScheduler!=null) {
                command.customScheduler.name = this.name+"::"+command.customScheduler.baseName;
            }
            commandsRunning.append(command.getDescriptor()).append(", ");
        }
        System.out.println(LogConfig.COMMAND_TAG + "(" + name + ") active: " + commandsRunning.toString());
        while(!processQueue.isEmpty()) {
            Command command = processQueue.get(0);

            //cancelled
            if(!command.active) {
                processQueue.remove(command);
                activeCommands.remove(command);
                continue;
            }

            boolean timeout = (command.timeoutTimer!=null && command.timeoutTimer.milliseconds()>command.timeoutMS);
            if(command.run() || timeout) {
                if(timeout) System.out.println(LogConfig.COMMAND_TAG +" Command " + command.getDescriptor() + " timed out");
                command.active = false;
                processQueue.remove(command);
                activeCommands.remove(command);
                if(command.children.isEmpty()) System.out.println(LogConfig.COMMAND_TAG + "(" + name + "): exiting from " + command.getDescriptor());
                else command.children.forEach(c -> {
                    if(!processQueue.contains(c)) {
                        System.out.println(LogConfig.COMMAND_TAG + "(" + name + "): transitioning from " + command.getDescriptor() + " to " + c.getDescriptor());
                        runCommand(c);
                        processQueue.add(c);
                    }
                });
            } else {
                processQueue.remove(0);
            }
        }
    }

    public void stopAll() {
        for(Command command : activeCommands) {
            command.active = false;
        }
    }
}