package com.vox.command;

import java.awt.Desktop;
import java.io.File;

public class CommandExecutor {
    public void execute(String action) {
        try {
            String home = System.getProperty("user.home");
            switch (action) {
                // папки
                case "open_downloads" -> Desktop.getDesktop().open(new File(home + "/Downloads"));
                case "open_documents" -> Desktop.getDesktop().open(new File(home + "/Documents"));
                case "open_desktop" -> Desktop.getDesktop().open(resolveDesktop(home));
                case "open_pictures" -> Desktop.getDesktop().open(new File(home + "/Pictures"));

                // программы
                case "open_browser" -> Runtime.getRuntime().exec("cmd /c start chrome");
                case "open_notepad" -> Runtime.getRuntime().exec("notepad.exe");
                case "open_calculator" -> Runtime.getRuntime().exec("calc.exe");
                case "open_explorer" -> Runtime.getRuntime().exec("explorer.exe");

                // система
                case "shutdown" -> Runtime.getRuntime().exec("shutdown /s /t 10");
                case "restart" -> Runtime.getRuntime().exec("shutdown /r /t 10");
                case "lock" -> Runtime.getRuntime().exec("rundll32.exe user32.dll,LockWorkStation");

                default -> System.out.println("Не понял команду");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File resolveDesktop(String home) {
        // на некоторых системах рабочий стол перенесён в OneDrive
        File plain = new File(home + "/Desktop");
        if (plain.exists()) return plain;

        File oneDriveEn = new File(home + "/OneDrive/Desktop");
        if (oneDriveEn.exists()) return oneDriveEn;

        File oneDriveRu = new File(home + "/OneDrive/Рабочий стол");
        if (oneDriveRu.exists()) return oneDriveRu;

        // если ничего не нашли — вернём стандартный путь, ошибка будет явной
        return plain;
    }
}