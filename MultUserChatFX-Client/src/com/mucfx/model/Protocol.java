package com.mucfx.model;

import java.util.ArrayList;

public class Protocol {
   private ArrayList<String> login;
   private ArrayList<String> logoff;
   private char commandSplitter;
   private char userSplitter;
   private ArrayList<String> allUsers;
   private ArrayList<String> serverToClientMessage;
   private ArrayList<String> clientToServerMessage;
   private ArrayList<String> users;

   public Protocol(ArrayList<String> login, ArrayList<String> logoff, char commandSplitter, char userSplitter, ArrayList<String> allUsers, ArrayList<String> serverToClientMessage, ArrayList<String> clientToServerMessage, ArrayList<String> users) {
      this.login = login;
      this.logoff = logoff;
      this.commandSplitter = commandSplitter;
      this.userSplitter = userSplitter;
      this.allUsers = allUsers;
      this.serverToClientMessage = serverToClientMessage;
      this.clientToServerMessage = clientToServerMessage;
      this.users = users;
   }

   public ArrayList<String> getLogin() {
      return login;
   }

   public ArrayList<String> getLogoff() {
      return logoff;
   }

   public char getCommandSplitter() {
      return commandSplitter;
   }

   public char getUserSplitter() {
      return userSplitter;
   }

   public ArrayList<String> getAllUsers() {
      return allUsers;
   }

   public ArrayList<String> getServerToClientMessage() {
      return serverToClientMessage;
   }

   public ArrayList<String> getClientToServerMessage() {
      return clientToServerMessage;
   }

   public ArrayList<String> getUsers() {
      return users;
   }

   public String getProtocolLogin() {
      String p = "";
      for(String s : login){
         p += "\""+s+"\";";
      }
      return p;
   }

   public String getProtocolLogoff() {
      String p = "";
      for(String s : logoff){
         p += "\""+s+"\";";
      }
      return p;
   }

   public String getProtocolAllUsers() {
      String p = "";
      for(String s : allUsers){
         p += "\""+s+"\";";
      }
      return p;
   }

   public String getProtocolServerToClientMessage() {
      String p = "";
      for(String s : serverToClientMessage){
         p += "\""+s+"\";";
      }
      return p;
   }

   public String getProtocolClientToServerMessage() {
      String p = "";
      for(String s : clientToServerMessage){
         p += "\""+s+"\";";
      }
      return p;
   }

   public String getProtocolUsers() {
      String p = "";
      for(String s : users){
         p += "\""+s+"\";";
      }
      return p;
   }

   @Override
   public String toString() {
      return "Protocol{" +
                   "login=" + login +
                   ", logoff=" + logoff +
                   ", commandSplitter=" + commandSplitter +
                   ", userSplitter=" + userSplitter +
                   ", allUsers=" + allUsers +
                   ", serverToClientMessage=" + serverToClientMessage +
                   ", clientToServerMessage=" + clientToServerMessage +
                   ", users=" + users +
                   '}';
   }
}
