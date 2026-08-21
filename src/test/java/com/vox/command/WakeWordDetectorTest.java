package com.vox.command;

import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class WakeWordDetectorTest {

    private final WakeWordDetector detector = new WakeWordDetector("вокс", "бокс");

    @Test
    void extractsCommandAfterWakeWord() {
        Optional<String> result = detector.extractCommand("вокс открой загрузки");
        assertTrue(result.isPresent());
        assertEquals("открой загрузки", result.get());
    }

    @Test
    void worksWithAlternativeWakeWord() {
        Optional<String> result = detector.extractCommand("бокс открой браузер");
        assertTrue(result.isPresent());
        assertEquals("открой браузер", result.get());
    }

    @Test
    void returnsEmptyWhenNoWakeWord() {
        Optional<String> result = detector.extractCommand("открой загрузки");
        assertTrue(result.isEmpty());
    }

    @Test
    void isCaseInsensitive() {
        Optional<String> result = detector.extractCommand("ВОКС открой документы");
        assertTrue(result.isPresent());
        assertEquals("открой документы", result.get());
    }
}
