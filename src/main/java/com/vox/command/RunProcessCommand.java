package com.vox.command;

public class RunProcessCommand implements VoiceCommand {
    private final String command;

    public RunProcessCommand(String command) {
        this.command = command;
    }

    @Override
    public void execute() throws Exception {
        Runtime.getRuntime().exec(command);
    }
}