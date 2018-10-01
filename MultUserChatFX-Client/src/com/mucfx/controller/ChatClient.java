package com.mucfx.controller;

import com.mucfx.model.Message;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import static com.mucfx.controller.Main.changeScreen;

public class ChatClient extends Thread{
   private final Charset cs = StandardCharsets.UTF_8;
   private final String serverName;
   private final int serverPort;
   private String clientName = null;
   private Socket socket;
   private OutputStream serverOut;
   private boolean logged = false;

   private ArrayList<String> users = new ArrayList<>();

   public ArrayList<String> getUsers() {
      return users;
   }

   public ChatClient(String serverName, int serverPort) {
      this.serverName = serverName;
      this.serverPort = serverPort;
   }

   public void msg(String sendTo, String msgBody) throws IOException {
      char c = Main.getCurrentProtocol().getCommandSplitter();
      for(String s : Main.getCurrentProtocol().getClientToServerMessage()) {
         String cmd = s+c+sendTo+c+msgBody+"\n";
         serverOut(cmd);
      }
   }

   @Override
   public void run() {
      readMessageLoop();
   }

   public void setClientName(String clientName) {
      this.clientName = clientName;
   }

   public String getClientName(){
      return clientName;
   }

   public boolean login(String login) throws IOException {
      this.start();
      char c = Main.getCurrentProtocol().getCommandSplitter();
      for(String s: Main.getCurrentProtocol().getLogin()) {
         String cmd = s+c+login+"\n";
         serverOut.write(cmd.getBytes(cs));
         sleep(1000);
      }

      if (logged) {
         System.out.println("logged true");
         this.clientName = login;
         return true;
      } else {
         System.out.println("logged false");
         this.interrupt();
         return false;
      }
   }

   public void logoff(){
      for(String s : Main.getCurrentProtocol().getLogoff()){
         String cmd = s+"\n";
         serverOut(cmd);
      }
   }

   private synchronized void readMessageLoop() {
      try {
         InputStream serverIn = socket.getInputStream();
         BufferedReader reader = new BufferedReader(new InputStreamReader(serverIn, cs));
         String line;
         while ((line = reader.readLine()) != null) {
            //sleep(1000);
            System.out.println("Input: "+line);
            String c = String.valueOf(Main.getCurrentProtocol().getCommandSplitter());
            String[] tokens = StringUtils.split(line, c);
            if (tokens != null && tokens.length > 0) {
               String cmd = tokens[0];
               if (contains(cmd, Main.getCurrentProtocol().getLogin())){
                  String[] tokensLogin = StringUtils.split(line, c, 2);
                  if(tokensLogin[1].equalsIgnoreCase("true")) {
                     logged = true;
                  }
                  changeScreen("logged", this);
               }else if (contains(cmd, Main.getCurrentProtocol().getUsers())) {
                  String[] tokensList = StringUtils.split(line, c, 2);
                  if (tokensList.length == 2)
                     handleList(tokensList);
               } else if (contains(cmd, Main.getCurrentProtocol().getServerToClientMessage())){
                  String[] tokensMsg = StringUtils.split(line, c, 4);
                  if (tokensMsg.length == 4)
                     handleMessage(tokensMsg);
               }
            }
         }
      } catch (Exception ex) {
         ex.printStackTrace();
         try {
            socket.close();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   private synchronized void handleList(String[] tokensList) {
      users = new ArrayList<String>(Arrays.asList(StringUtils.split(tokensList[1], Main.getCurrentProtocol().getUserSplitter())));
      changeScreen("users", users);
      sleep(1000);
   }

   private void handleMessage(String[] tokensMsg) {
      changeScreen("message", new Message(tokensMsg[1],
         StringUtils.split(tokensMsg[2], Main.getCurrentProtocol().getUserSplitter()),tokensMsg[3]));
   }

   public boolean connect() {
      try {
         this.socket = new Socket(serverName, serverPort);
         this.serverOut = socket.getOutputStream();
         return true;
      } catch (IOException e) {
         e.printStackTrace();
      }
      return false;
   }

   private void serverOut(String cmd){
      try{
         serverOut.write(cmd.getBytes(cs));
      }catch(Exception e) {
         if(e instanceof SocketException)
            if(connect()){
            sleep(100);
            try {
               login(clientName);
            } catch (IOException e1) {
               e1.printStackTrace();
            }

            }
         e.printStackTrace();
      }
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

   public OutputStream getServerOut() {
      return serverOut;
   }
}
