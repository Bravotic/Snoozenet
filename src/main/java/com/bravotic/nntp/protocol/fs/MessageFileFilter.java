package com.bravotic.nntp.protocol.fs;

import java.io.File;
import java.io.FileFilter;

public class MessageFileFilter implements FileFilter {
    @Override
    public boolean accept(File pathname) {
        return pathname.isFile() && pathname.getName().matches("^[0-9]*\\.eml$");
    }
}
