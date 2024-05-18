package com.bravotic.nntp.protocol.fs;

import com.bravotic.nntp.protocol.Article;
import com.bravotic.nntp.protocol.Group;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class FSGroup implements Group {
    private final String name;

    private int min;

    private int max;

    private int total;

    private final File dir;

    private final boolean isPostingAllowed;

    private void refresh() {
        File[] messages = dir.listFiles(new MessageFileFilter());
        if (messages != null && messages.length != 0) {

            min = Integer.parseInt(messages[0].getName().split("\\.")[0]);
            max = min;
            total = messages.length;

            for (File message : messages) {
                int id = Integer.parseInt(message.getName().split("\\.")[0]);

                if (id > max) {
                    max = id;
                }

                if (id < min) {
                    min = id;
                }
            }
        }
        else {
            max = 0;
            min = 0;
            total = 0;
        }
    }

    private File openID(int id) {
        return new File(dir.getPath() + File.separator + id + ".eml");
    }
    public FSGroup(String name, File dir) {
        File readonly = new File(dir.getPath() + "/.readonly");

        this.dir = dir;
        this.isPostingAllowed = !readonly.exists();

        this.refresh();

        this.name = name;

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getLast() {
        this.refresh();
        return max;
    }

    @Override
    public int getFirst() {
        this.refresh();
        return min;
    }

    @Override
    public int getTotal() {
        return total;
    }

    @Override
    public boolean isPostingAllowed() {
        return isPostingAllowed;
    }

    @Override
    public boolean post(String content) {
        // TODO: Probably should put this behind a mutex
        int thisID = this.max + 1;
        File postedFile = new File(dir.getPath() + File.separator + thisID + ".eml");

        try {
            if (postedFile.createNewFile()) {
                Article a = new Article(content, name + " " + name + ":" + thisID);

                FileWriter fw = new FileWriter(postedFile);
                fw.write(a.getArticle());
                fw.close();

                this.refresh();

                return true;
            }
            else {
                return false;
            }
        }
        catch (IOException e) {
            return false;
        }
    }

    public Article article(int id) {
        File message = this.openID(id);

        if (message.exists()) {
            try {
                FileReader fr = new FileReader(message);
                Scanner sc = new Scanner(fr);
                StringBuilder sb = new StringBuilder();

                while(sc.hasNextLine()) {
                    sb.append(sc.nextLine());
                    sb.append("\r\n");
                }

                return new Article(sb.toString());
            }
            catch(IOException e) {
                return null;
            }
        }
        return null;
    }
}
