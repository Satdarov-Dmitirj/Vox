package com.vox.command;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CommandRegistryTest {

    private final CommandRegistry registry = new CommandRegistry();

    @Test
    void findsFolderCommandByKeyword() {
        VoiceCommand command = registry.findCommand("открой загрузки");
        assertNotNull(command);
        assertInstanceOf(OpenFolderCommand.class, command);
    }

    @Test
    void findsProcessCommandByKeyword() {
        VoiceCommand command = registry.findCommand("открой калькулятор");
        assertNotNull(command);
        assertInstanceOf(RunProcessCommand.class, command);
    }

    @Test
    void returnsNullForUnknownText() {
        VoiceCommand command = registry.findCommand("сделай мне кофе");
        assertNull(command);
    }

    @Test
    void findsKeywordAnywhereInPhrase() {
        VoiceCommand command = registry.findCommand("пожалуйста открой браузер сейчас");
        assertNotNull(command);
        assertInstanceOf(RunProcessCommand.class, command);
    }
}
