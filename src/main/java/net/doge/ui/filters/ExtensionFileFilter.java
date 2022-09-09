package net.doge.ui.filters;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.ArrayList;

public class ExtensionFileFilter extends FileFilter {
    private String description;
    private ArrayList<String> extensions = new ArrayList<>();

    @Override
    public boolean accept(File f) {
        if(f.isDirectory()) return true;
        String filename = f.getName().toLowerCase();
        for (String extension: extensions) {
            if(filename.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addExtension(String extension) {
        if(!extension.startsWith(".")) {
            extension = "." + extension;
        }
        extensions.add(extension.toLowerCase());
    }

    @Override
    public String getDescription() {
        return description;
    }
}
