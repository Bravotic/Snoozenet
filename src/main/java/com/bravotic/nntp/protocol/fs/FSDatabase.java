package com.bravotic.nntp.protocol.fs;

import com.bravotic.nntp.protocol.Group;
import com.bravotic.nntp.protocol.GroupsDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FSDatabase implements GroupsDatabase {
    private List<Group> database;

    private void addGroupToDatabase(String prefix, File dir) {
        File[] dirListing = dir.listFiles();

        if (dirListing != null) {
            // TODO: Filter this listing
            for (File entry : dirListing) {
                if (entry.isDirectory() && !entry.getName().startsWith(".")) {
                    String entryPrefix;

                    if (prefix.isEmpty()) {
                        entryPrefix = entry.getName();
                    }
                    else {
                        entryPrefix = prefix + "." + entry.getName();
                    }

                    addGroupToDatabase(entryPrefix, entry);
                }
                else if (entry.getName().equals(".group")) {
                    database.add(new FSGroup(prefix, dir));
                }
            }
        }
    }

    public FSDatabase(String directory) {

        // Load our starting directory
        File root = new File(directory);

        database = new ArrayList<>();

        // Load our database from our root.
        addGroupToDatabase("", root);
    }

    @Override
    public Group[] list() {
        return database.toArray(new Group[0]);
    }

    @Override
    public Group[] list(String search) {
        return new Group[0];
    }

    @Override
    public Group selectGroup(String name) {
        for (Group g : database) {
            if (g.getName().equals(name)) {
                return g;
            }
        }
        return null;
    }

}
