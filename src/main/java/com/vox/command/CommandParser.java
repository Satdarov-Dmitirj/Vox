package com.vox.command;

public class CommandParser {
    public String parse(String text) {
        text = text.toLowerCase();

        // папки
        if (text.contains("загрузки")) return "open_downloads";
        if (text.contains("документы")) return "open_documents";
        if (text.contains("рабочий стол")) return "open_desktop";
        if (text.contains("изображения") || text.contains("картинки")) return "open_pictures";

        // программы
        if (text.contains("браузер") || text.contains("хром")) return "open_browser";
        if (text.contains("блокнот")) return "open_notepad";
        if (text.contains("калькулятор")) return "open_calculator";
        if (text.contains("проводник")) return "open_explorer";

        // система
        if (text.contains("выключи компьютер")) return "shutdown";
        if (text.contains("перезагрузи")) return "restart";
        if (text.contains("заблокируй")) return "lock";

        return "unknown";
    }
}