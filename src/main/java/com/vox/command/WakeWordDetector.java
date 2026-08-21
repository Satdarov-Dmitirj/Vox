package com.vox.command;

import java.util.Optional;

public class WakeWordDetector {
    private final String[] wakeWords;

    public WakeWordDetector(String... wakeWords) {
        this.wakeWords = wakeWords;
    }

    public Optional<String> extractCommand(String text) {
        String lower = text.toLowerCase();
        for (String wake : wakeWords) {
            if (lower.contains(wake)) {
                String command = lower.substring(lower.indexOf(wake) + wake.length()).trim();
                return Optional.of(command);
            }
        }
        return Optional.empty();
    }
}