package com.vox.command;

import java.awt.Desktop;
import java.io.File;

public class OpenFolderCommand implements VoiceCommand {
    private final File folder;

    public OpenFolderCommand(File folder) {
        this.folder = folder;
    }

    @Override
    public void execute() throws Exception {
        Desktop.getDesktop().open(folder);
    }
}