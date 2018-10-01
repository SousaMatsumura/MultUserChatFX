package com.mucfx.controller;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;


public class ServerWorker extends Thread {

   private final Charset cs = StandardCharsets.UTF_8;
   private final Socket clientSocket;
   private final Server server;
   private int port;
   private String login = null;
   private OutputStream outputStream;

   public ServerWorker(Server server, Socket clientSocket, int port) {
      this.server = server;
      this.clientSocket = clientSocket;
      this.port = port;
   }

   public ServerWorker(Server server, Socket clientSocket, int port, String login) {
      this.server = server;
      this.clientSocket = clientSocket;
      this.port = port;
      this.login = login;
   }

   private void onChangeListener() {
      Main.addOnChangeScreenListener(new Main.OnChangeScreen() {
         @Override
         public void onScreenChanged(String newScreen, Object userData) {
            if (newScreen.equals("close")) {
               Thread.currentThread().interrupt();
            }
         }
      });
   }

   @Override
   public void run() {
      try {
         handleClientSocket();
      }catch (SocketException e){
         try {
            if(e.getMessage().equals("Connection reset")){
               server.removeWorker(this);
            }else if(e.getMessage().equals("Socket closed")){
               ServerWorker temp = new ServerWorker(server, new Socket(), port, login);
               temp.handleLogoff();
               temp.clientSocket.close();
               temp.interrupt();
               clientSocket.close();
            }
         } catch (IOException e1) {
            e1.printStackTrace();
         }
         this.interrupt();
         e.printStackTrace();
      }catch (IOException e) {
         e.printStackTrace();
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }

   private void handleClientSocket() throws IOException, InterruptedException {
      InputStream inputStream = clientSocket.getInputStream();
      this.outputStream = clientSocket.getOutputStream();


      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, cs));
      String line;
      while ((line = reader.readLine()) != null) {
         if(Main.getState().equals("close")){
            this.interrupt();
         }
         String splitter = String.valueOf(Main.getCurrentProtocol().getCommandSplitter());
         System.out.println(line);
         String[] tokens = StringUtils.split(line, splitter);
         if (tokens != null && tokens.length > 0) {
            String cmd = tokens[0];
            if (contains(cmd, Main.getCurrentProtocol().getLogoff())){
               handleLogoff();
               break;
            } else if (contains(cmd, Main.getCurrentProtocol().getLogin())){
               String[] tokensLogin = StringUtils.split(line, splitter, 2);
               if(tokensLogin.length == 2)
                  handleLogin(outputStream, tokensLogin);
            } else if (contains(cmd, Main.getCurrentProtocol().getClientToServerMessage()) && login != null) {
               String[] tokensMsg = StringUtils.split(line, splitter, 3);
               if(tokensMsg.length == 3)
                  handleMessage(tokensMsg);
            }
         }
      }
      clientSocket.close();
   }


   private void handleMessage(String[] tokens) throws IOException {
      String sendTo[] = StringUtils.split(tokens[1], String.valueOf(Main.getCurrentProtocol().getUserSplitter()));
      String body = tokens[2];

      List<ServerWorker> workerList = server.getWorkerList();
      String q = String.valueOf(Main.getCurrentProtocol().getCommandSplitter());
      if(contains(sendTo[0], Main.getCurrentProtocol().getAllUsers())){
         for (ServerWorker worker : workerList) {
            if(worker.login != null) {
               for (String s : Main.getCurrentProtocol().getAllUsers()){
                  for (String r : Main.getCurrentProtocol().getServerToClientMessage()) {
                     String outMsg = r+q+login+q+s+q+body+"\n";
                     worker.send(outMsg, 1000);
                  }
               }
            }
         }
      }else {
         for (ServerWorker worker : workerList) {
            for (String s : sendTo) {
               if (worker.getLogin()!=null && worker.getLogin().equals(s)) {
                  for (String r : Main.getCurrentProtocol().getServerToClientMessage()) {
                     String outMsg = r+q+login+q+tokens[1]+q+body+"\n";
                     worker.send(outMsg, 1000);
                  }
               }
            }
         }
      }
   }

   private void handleLogoff() throws IOException {
      server.removeWorker(this);
      sendUserList();
      clientSocket.close();
   }

   private void sendUserList() {
      for(String s : Main.getCurrentProtocol().getUsers()){
         String userList = s + String.valueOf(Main.getCurrentProtocol().getCommandSplitter());
         List<ServerWorker> workerList = server.getWorkerList();
         for (ServerWorker worker : workerList) {
            if (worker != null && worker.getLogin() != null) {
               userList += worker.getLogin() + String.valueOf(Main.getCurrentProtocol().getUserSplitter());
            }
         }
         if (userList.length() > 0)
            userList = userList.substring(0, userList.length() - 1);
         userList += "\n";
         try {
            for (Iterator<ServerWorker> it = workerList.iterator(); it.hasNext(); ) {
               ServerWorker worker = it.next();
               if (worker.getLogin() != null) {
                  worker.send(userList, 1000);
               }
            }
         } catch (IOException | ConcurrentModificationException e) {
            e.printStackTrace();
         }
      }
   }

   public String getLogin() {
      return login;
   }

   private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
      List<ServerWorker> workerList = server.getWorkerList();
      String login = tokens[1];
      if (workerList.get(0) != null && inedit(login)) {
         for (String s : Main.getCurrentProtocol().getLogin()){
            String msg = s + String.valueOf(Main.getCurrentProtocol().getCommandSplitter()) + "true\n";
            outputStream.write(msg.getBytes(cs));
            sleep(1000);
         }
         this.login = login;
         sendUserList();
      } else {
         for(String s : Main.getCurrentProtocol().getLogin()) {
            String msg = s+String.valueOf(Main.getCurrentProtocol().getCommandSplitter())+"false\n";
            outputStream.write(msg.getBytes(cs));
         }
      }
   }

   private boolean inedit(String login) {
      List<ServerWorker> workerList = server.getWorkerList();
      if(contains(login,  Main.getCurrentProtocol().getAllUsers(),
         new ArrayList<String>(){{add(String.valueOf(Main.getCurrentProtocol().getUserSplitter()));}})){
         return false;
      }
      if(!(workerList.size() == 1)){
         for (ServerWorker worker : workerList) {
            if (worker.getLogin() != null && worker.getLogin().equals(login)) {
               return false;
            }
         }
      }
      return true;
   }

   private void sleep(int i){
      try {
         Thread.sleep(i);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }

   private boolean contains(String e, ArrayList<String>... set){
      for(ArrayList<String> al : set){
         for(String s : al) {
            if (s.equalsIgnoreCase(e)) {
               return true;
            }
         }
      }
      return false;
   }

   private void send(String msg, int i) throws IOException {
      if (login != null) {
         try {
            outputStream.write(msg.getBytes(cs));
            sleep(i);
         } catch(Exception ex) {
            if(ex instanceof SocketException)
               server.removeWorker(this);
            ex.printStackTrace();
         }
      }
   }
}
