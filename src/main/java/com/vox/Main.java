package com.vox;

import com.vox.recognizer.SpeechToText;
import com.vox.command.CommandRegistry;
import com.vox.command.VoiceCommand;
import com.vox.command.WakeWordDetector;
import com.vox.speaker.TextToSpeech;
import com.vox.llm.LlmCommandInterpreter;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws Exception {
        SpeechToText stt = new SpeechToText("src/main/resources/vosk-model-ru-0.42");
        CommandRegistry registry = new CommandRegistry();
        WakeWordDetector wakeWordDetector = new WakeWordDetector("вокс", "бокс");
        TextToSpeech tts = new TextToSpeech();
        LlmCommandInterpreter llm = new LlmCommandInterpreter();

        ExecutorService commandExecutor = Executors.newSingleThreadExecutor();

        Thread listenerThread = new Thread(() -> {
            try {
                stt.listen(text -> commandExecutor.submit(() ->
                        handleText(text, registry, wakeWordDetector, tts, llm)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();

        System.out.println("Vox запущен. Нажми Enter, чтобы завершить работу.");
        System.in.read();
        commandExecutor.shutdownNow();
    }

    private static void handleText(String text, CommandRegistry registry, WakeWordDetector wakeWordDetector,
                                   TextToSpeech tts, LlmCommandInterpreter llm) {
        System.out.println("Услышал: " + text);

        Optional<String> commandText = wakeWordDetector.extractCommand(text);
        if (commandText.isEmpty()) {
            return;
        }
        String phrase = commandText.get();
        System.out.println("Команда после wake word: " + phrase);

        VoiceCommand command = registry.findCommand(phrase);

        if (command == null) {
            String action = llm.interpret(phrase);
            System.out.println("LLM определил действие: " + action);
            if (!action.equals("unknown")) {
                command = registry.findByAction(action);
            }
        }

        if (command != null) {
            try {
                command.execute();
                tts.speak("Выполняю");
            } catch (Exception e) {
                e.printStackTrace();
                tts.speak("Не получилось выполнить");
            }
            return;
        }

        String reply = llm.chat(phrase);
        System.out.println("Ответ LLM: " + reply);
        if (!reply.isBlank()) {
            tts.speak(reply);
        } else {
            tts.speak("Извини, не поняла");
        }
    }
}