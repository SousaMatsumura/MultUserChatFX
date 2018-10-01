package com.mucfx.controller;

import com.mucfx.model.Protocol;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.Iterator;

public class Main extends Application {

   private static Stage stage;
   private static Scene serverScene;
   private static Scene protocolScene;
   private static String state = "";
   private static final Protocol defaultProtocol = new Protocol(
      new ArrayList<String>(){{add("login");}},
      new ArrayList<String>(){{add("logoff"); add("quit"); add("sair");}},
      ':', ';', new ArrayList<String>(){{add("*");}},
      new ArrayList<String>(){{add("transmitir");}},
      new ArrayList<String>(){{add("mensagem");}},
      new ArrayList<String>(){{add("lista_usuarios");}});
   private static Protocol currentProtocol = defaultProtocol;

   @Override
   public void start(Stage primaryStage) throws Exception{

//      //------------------Testes--------------------------

//      //------------------Testes--------------------------//

      stage = primaryStage;

      primaryStage.setTitle("MultiUserChatFX Server");

      stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
         @Override
         public void handle(WindowEvent event) {
            notifyAllListeners("close", null);
            Platform.exit();
            System.exit(0);
         }});

      Parent fxmlLogin = FXMLLoader.load(getClass().getResource("/com/mucfx/view/server.fxml"));
      serverScene = new Scene(fxmlLogin, 290, 150);

      Parent fxmlProtocol = FXMLLoader.load(getClass().getResource("/com/mucfx/view/protocol.fxml"));
      protocolScene = new Scene(fxmlProtocol, 650, 450);

      primaryStage.setScene(serverScene);
      primaryStage.setResizable(false);
      primaryStage.show();
   }

   @FXML
   public synchronized static void changeScreen(String s, Object userData){
      state = s;
      switch(s) {
         case "server":
            stage.setScene(serverScene);
            stage.setResizable(false);
            notifyAllListeners("server", userData);
            break;
         case "exception":
            stage.setScene(serverScene);
            notifyAllListeners("exception", userData);
            break;
         case "protocol":
            stage.setScene(protocolScene);
            stage.setResizable(true);
            notifyAllListeners("protocol", userData);
            break;
         case "close":
            Thread.currentThread().interrupt();
            notifyAllListeners("close", userData);
      }
   }

   public static void changeScreen(String s){
      changeScreen(s, null);
   }

   public static void main(String[] args) {
      launch(args);
   }

   private static ArrayList<OnChangeScreen> listeners = new ArrayList<>();

   public static interface OnChangeScreen{
      void onScreenChanged(String newScreen, Object userData);
   }

   public static void addOnChangeScreenListener(OnChangeScreen newListener){
      listeners.add(newListener);
   }

   private synchronized static void notifyAllListeners(String newScreen, Object userData){
      for(Iterator<OnChangeScreen> it = listeners.iterator(); it.hasNext();){
         OnChangeScreen l = it.next();
         l.onScreenChanged(newScreen, userData);
      }
   }

   public static String getState(){
      return state;
   }

   public static Protocol getDefaultProtocol() {
      return defaultProtocol;
   }

   public static Protocol getCurrentProtocol() {
      return currentProtocol;
   }

   public static void setCurrentProtocol(Protocol protocol){
      currentProtocol = protocol;
   }
}