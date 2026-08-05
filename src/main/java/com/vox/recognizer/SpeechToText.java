package com.vox.recognizer;

import org.vosk.Model;
import org.vosk.Recognizer;
import javax.sound.sampled.*;
import java.util.function.Consumer;

public class SpeechToText {
    private final Model model;

    public SpeechToText(String modelPath) throws Exception {
        this.model = new Model(modelPath);
    }

    public void listen(Consumer<String> onResult) throws Exception {
        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine mic = (TargetDataLine) AudioSystem.getLine(info);
        mic.open(format);
        mic.start();

        Recognizer recognizer = new Recognizer(model, 16000);
        byte[] buffer = new byte[4096];
        String lastPartial = "";

        System.out.println("Слушаю...");
        while (true) {
            int bytesRead = mic.read(buffer, 0, buffer.length);
            if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                String text = extractField(recognizer.getResult(), "text");
                if (!text.isBlank()) {
                    onResult.accept(text);
                }
                lastPartial = "";
            } else {
                String partial = extractField(recognizer.getPartialResult(), "partial");
                System.out.println("partial: " + partial);
                if (partial.isBlank() && !lastPartial.isBlank()) {
                    String text = extractField(recognizer.getFinalResult(), "text");
                    if (!text.isBlank()) {
                        onResult.accept(text);
                    }
                }
                lastPartial = partial;
            }
        }
    }

    private String extractField(String json, String key) {
        String search = "\"" + key + "\"";
        int keyIdx = json.indexOf(search);
        if (keyIdx < 0) return "";
        int colonIdx = json.indexOf(':', keyIdx + search.length());
        if (colonIdx < 0) return "";
        int firstQuote = json.indexOf('"', colonIdx);
        if (firstQuote < 0) return "";
        int secondQuote = json.indexOf('"', firstQuote + 1);
        if (secondQuote < 0) return "";
        return json.substring(firstQuote + 1, secondQuote);
    }
}