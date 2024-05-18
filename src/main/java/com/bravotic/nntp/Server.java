package com.bravotic.nntp;

import com.bravotic.nntp.protocol.GroupsDatabase;
import com.bravotic.nntp.protocol.Handler;
import com.bravotic.nntp.protocol.fs.FSDatabase;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private static ServerSocket server;
    private static GroupsDatabase db;

    public static void main(String[] args) {
        ServerConfigReader config = new ServerConfigReader();

        if (config.getBackend().equals("fs")) {
            db = new FSDatabase(config.getFSPath());
        }
        else {
            throw new IllegalStateException("Invalid backend selected");
        }

        try {
            server = new ServerSocket(config.getPort());
            System.out.println("Snoozenet has started. Listening on port " + config.getPort());
            while (true) {
                Socket client = server.accept();
                Thread t = new Thread(new Controller(client.getInputStream(), client.getOutputStream(), db));
                t.start();
            }
        }
        catch (IOException e) {
            System.out.println("There was an error starting the server");
        }



    }
}
