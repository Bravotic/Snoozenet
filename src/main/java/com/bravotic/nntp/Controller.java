package com.bravotic.nntp;

import com.bravotic.nntp.protocol.GroupsDatabase;
import com.bravotic.nntp.protocol.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class Controller implements Runnable {

    private GroupsDatabase db;
    private InputStream in;
    private OutputStream out;

    public Controller (InputStream in, OutputStream out, GroupsDatabase db) {
        this.in = in;
        this.out = out;
        this.db = db;
    }

    @Override
    public void run() {
        try {
            Handler nntp = new Handler(db);
            Scanner input = new Scanner(in);

            out.write(nntp.generic().getBytes());

            while (input.hasNextLine()) {
                String commandLine = input.nextLine();
                String[] commandParts = commandLine.split(" ");
                int id;

                switch (commandParts[0]) {
                    case "LIST":
                        out.write(nntp.list("").getBytes());
                        break;
                    case "GROUP":
                        out.write(nntp.group(commandParts[1]).getBytes());
                        break;
                    case "POST":
                        String resp = nntp.post();
                        out.write(resp.getBytes());

                        // If it's okay for us to post, start reading in the message content
                        if (resp.startsWith("340")) {
                            StringBuilder content = new StringBuilder();
                            String line = input.nextLine();

                            // While we have content to read, read it
                            while (!line.equals(".")) {
                                content.append(line);
                                content.append("\r\n");
                                line = input.nextLine();
                            }

                            // Then just write it back to the handler
                            out.write(nntp.post(content.toString()).getBytes());
                        }
                        else {
                            out.write(resp.getBytes());
                        }
                        break;
                    case "HEAD":
                        id = Integer.parseInt(commandParts[1]);
                        out.write(nntp.head(id).getBytes());
                        break;
                    case "ARTICLE":
                        id = Integer.parseInt(commandParts[1]);
                        out.write(nntp.article(id).getBytes());
                        break;
                    case "XOVER":
                        out.write(nntp.xover().getBytes());
                        break;
                    default:
                        System.out.println("[Warning] We got the command '" + commandLine + "' from a client which we " +
                                "don't understand. Replying with '500 command not recognized'. This should be reported.");
                        out.write("500 command not recognized\r\n".getBytes());
                        break;
                }
            }
        }
        catch (IOException e) {

        }
    }
}
