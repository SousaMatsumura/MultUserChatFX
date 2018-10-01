package com.mucfx.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server extends Thread {
   private final int serverPort;
   private ArrayList<ServerWorker> workerList = new ArrayList<>();

   public Server(int serverPort) {
      this.serverPort = serverPort;
   }

   public ArrayList<ServerWorker> getWorkerList() {
      return workerList;
   }

   @Override
   public void run() {
      try {
         ServerSocket serverSocket = new ServerSocket(serverPort);
         while(true) {
            if(Main.getState().equals("close")){
               this.interrupt();
            }
            System.out.println("About to accept client connection...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Accepted connection from " + clientSocket);
            ServerWorker worker = new ServerWorker(this, clientSocket, serverPort);
            workerList.add(worker);
            worker.start();
         }
      } catch (IOException e) {
         Main.changeScreen("exception",e);
         e.printStackTrace();
      }
   }

   public void removeWorker(ServerWorker serverWorker) {
      workerList.remove(serverWorker);
   }
}
