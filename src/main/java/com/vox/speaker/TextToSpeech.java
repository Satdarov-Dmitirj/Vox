package com.vox.speaker;

public class TextToSpeech {

    public void speak(String text) {
        try {
            String escaped = text.replace("'", "''"); // экранируем кавычки для PowerShell
            String command = "powershell -Command \"Add-Type -AssemblyName System.Speech; " +
                    "(New-Object System.Speech.Synthesis.SpeechSynthesizer).Speak('" + escaped + "')\"";
            Runtime.getRuntime().exec(new String[]{"cmd", "/c", command});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
