package com.bravotic.nntp.protocol;

public interface Group {
    String getName();

    int getLast();

    int getFirst();

    int getTotal();

    boolean isPostingAllowed();

    boolean post(String content);

    Article article(int id);
}
