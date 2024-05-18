package com.bravotic.nntp.protocol;

public interface GroupsDatabase {
    Group[] list();
    Group[] list(String search);

    Group selectGroup(String name);
}
