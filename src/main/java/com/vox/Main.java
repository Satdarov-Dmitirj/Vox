package com.vox;

import com.vox.recognizer.SpeechToText;
import com.vox.command.CommandRegistry;
import com.vox.command.VoiceCommand;
import com.vox.command.WakeWordDetector;
import com.vox.speaker.TextToSpeech;
import com.vox.llm.LlmCommandInterpreter;

import java.util.Optional;

public class Main {
    public static void main(String[] args) throws Exception {
        SpeechToText stt = new SpeechToText("src/main/resources/vosk-model-small-ru-0.22");
        CommandRegistry registry = new CommandRegistry();
        WakeWordDetector wakeWordDetector = new WakeWordDetector("вокс", "бокс");
        TextToSpeech tts = new TextToSpeech();
        LlmCommandInterpreter llm = new LlmCommandInterpreter();

        Thread listenerThread = new Thread(() -> {
            try {
                stt.listen(text -> handleText(text, registry, wakeWordDetector, tts, llm));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();

        System.out.println("Vox запущен. Нажми Enter, чтобы завершить работу.");
        System.in.read();
    }

    private static void handleText(String text, CommandRegistry registry, WakeWordDetector wakeWordDetector,
                                   TextToSpeech tts, LlmCommandInterpreter llm) {
        System.out.println("Услышал: " + text);

        Optional<String> commandText = wakeWordDetector.extractCommand(text);
        if (commandText.isEmpty()) {
            return;
        }
        System.out.println("Команда после wake word: " + commandText.get());

        VoiceCommand command = registry.findCommand(commandText.get());

        if (command == null) {
            String action = llm.interpret(commandText.get());
            System.out.println("LLM определил действие: " + action);
            command = registry.findByAction(action);
        }

        if (command == null) {
            System.out.println("Не понял команду");
            tts.speak("Не поняла команду");
            return;
        }

        try {
            command.execute();
            tts.speak("Выполняю");
        } catch (Exception e) {
            e.printStackTrace();
            tts.speak("Не получилось выполнить");
        }
    }
}