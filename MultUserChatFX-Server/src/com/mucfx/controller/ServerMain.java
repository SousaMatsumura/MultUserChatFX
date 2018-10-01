package com.mucfx.controller;

/*Run this to up a server without the GUI*/

public class ServerMain {
    public static void main(String[] args) {
        int port = 8818;
        Server server = new Server(port);
        server.start();
    }
}