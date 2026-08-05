package com.vox;

import com.vox.recognizer.SpeechToText;
import com.vox.command.CommandParser;
import com.vox.command.CommandExecutor;

public class Main {
    private static final String[] WAKE_WORDS = {"вокс", "бокс"};

    public static void main(String[] args) throws Exception {
        SpeechToText stt = new SpeechToText("src/main/resources/vosk-model-small-ru-0.22");
        CommandParser parser = new CommandParser();
        CommandExecutor executor = new CommandExecutor();

        stt.listen(text -> {
            System.out.println("Услышал: " + text);

            String lower = text.toLowerCase();
            String matchedWakeWord = null;
            for (String wake : WAKE_WORDS) {
                if (lower.contains(wake)) {
                    matchedWakeWord = wake;
                    break;
                }
            }
            if (matchedWakeWord == null) {
                return;
            }

            String command = lower.substring(lower.indexOf(matchedWakeWord) + matchedWakeWord.length()).trim();
            System.out.println("Команда после wake word: " + command);

            String action = parser.parse(command);
            executor.execute(action);
        });
    }
}