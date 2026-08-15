package com.vox.command;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class CommandRegistry {
    private final Map<String, VoiceCommand> byKeyword = new LinkedHashMap<>();
    private final Map<String, VoiceCommand> byAction = new LinkedHashMap<>();

    public CommandRegistry() {
        String home = System.getProperty("user.home");

        register("загрузки", "open_downloads", new OpenFolderCommand(resolveFolder(home, "Downloads", "Загрузки")));
        register("документы", "open_documents", new OpenFolderCommand(resolveFolder(home, "Documents", "Документы")));
        register("рабочий стол", "open_desktop", new OpenFolderCommand(resolveFolder(home, "Desktop", "Рабочий стол")));
        register("изображения", "open_pictures", new OpenFolderCommand(resolveFolder(home, "Pictures", "Изображения")));

        register("браузер", "open_browser", new RunProcessCommand("cmd /c start chrome"));
        register("блокнот", "open_notepad", new RunProcessCommand("notepad.exe"));
        register("калькулятор", "open_calculator", new RunProcessCommand("calc.exe"));
        register("проводник", "open_explorer", new RunProcessCommand("explorer.exe"));

        register("выключи компьютер", "shutdown", new RunProcessCommand("shutdown /s /t 10"));
        register("перезагрузи", "restart", new RunProcessCommand("shutdown /r /t 10"));
        register("заблокируй", "lock", new RunProcessCommand("rundll32.exe user32.dll,LockWorkStation"));
    }

    private void register(String keyword, String action, VoiceCommand command) {
        byKeyword.put(keyword, command);
        byAction.put(action, command);
    }

    public VoiceCommand findCommand(String text) {
        String lower = text.toLowerCase();
        for (Map.Entry<String, VoiceCommand> entry : byKeyword.entrySet()) {
            if (lower.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public VoiceCommand findByAction(String action) {
        return byAction.get(action);
    }

    private File resolveFolder(String home, String enName, String ruName) {
        File plain = new File(home + "/" + enName);
        if (plain.exists()) return plain;

        File oneDriveEn = new File(home + "/OneDrive/" + enName);
        if (oneDriveEn.exists()) return oneDriveEn;

        File oneDriveRu = new File(home + "/OneDrive/" + ruName);
        if (oneDriveRu.exists()) return oneDriveRu;

        return plain;
    }
}