package com.vox.command;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class CommandRegistry {
    private final Map<String, VoiceCommand> commands = new LinkedHashMap<>();

    public CommandRegistry() {
        String home = System.getProperty("user.home");

        // папки
        commands.put("загрузки", new OpenFolderCommand(new File(home + "/Downloads")));
        commands.put("документы", new OpenFolderCommand(new File(home + "/Documents")));
        commands.put("рабочий стол", new OpenFolderCommand(resolveDesktop(home)));
        commands.put("изображения", new OpenFolderCommand(new File(home + "/Pictures")));

        // программы
        commands.put("браузер", new RunProcessCommand("cmd /c start chrome"));
        commands.put("блокнот", new RunProcessCommand("notepad.exe"));
        commands.put("калькулятор", new RunProcessCommand("calc.exe"));
        commands.put("проводник", new RunProcessCommand("explorer.exe"));

        // система
        commands.put("выключи компьютер", new RunProcessCommand("shutdown /s /t 10"));
        commands.put("перезагрузи", new RunProcessCommand("shutdown /r /t 10"));
        commands.put("заблокируй", new RunProcessCommand("rundll32.exe user32.dll,LockWorkStation"));
    }

    public VoiceCommand findCommand(String text) {
        String lower = text.toLowerCase();
        for (Map.Entry<String, VoiceCommand> entry : commands.entrySet()) {
            if (lower.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private File resolveDesktop(String home) {
        File plain = new File(home + "/Desktop");
        if (plain.exists()) return plain;

        File oneDriveEn = new File(home + "/OneDrive/Desktop");
        if (oneDriveEn.exists()) return oneDriveEn;

        File oneDriveRu = new File(home + "/OneDrive/Рабочий стол");
        if (oneDriveRu.exists()) return oneDriveRu;

        return plain;
    }
}