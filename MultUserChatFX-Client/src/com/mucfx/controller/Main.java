package com.mucfx.controller;

import com.mucfx.model.Protocol;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.Iterator;

public class Main extends Application {

   final KeyCombination KB = new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN);
   private static Stage stage;
   private static Scene loginScene;
   private static Scene mainScene;
   private static Scene protocolScene;
   private static String state = "login";
   private static String prevState = "";
   private static ChatClient mainClient = null;
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

      stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
         @Override
         public void handle(WindowEvent event) {
            notifyAllListeners("close", null);
            Platform.exit();
            System.exit(0);
         }
      });

      primaryStage.setTitle("MultiUserChatFX Client");

      Parent fxmlLogin = FXMLLoader.load(getClass().getResource("/com/mucfx/view/login.fxml"));
      loginScene = new Scene(fxmlLogin, 300, 300);
      loginScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
         @Override
         public void handle(KeyEvent event) {
            if(KB.match(event)){
               changeScreen("protocol", mainClient);
            }
         }
      });

      Parent fxmlMain = FXMLLoader.load(getClass().getResource("/com/mucfx/view/chat_screen.fxml"));
      mainScene = new Scene(fxmlMain, 610, 410);
      mainScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
         @Override
         public void handle(KeyEvent event) {
            if(KB.match(event)){
               changeScreen("protocol", mainClient);
            }
         }
      });

      Parent fxmlProtocol = FXMLLoader.load(getClass().getResource("/com/mucfx/view/protocol.fxml"));
      protocolScene = new Scene(fxmlProtocol, 650, 450);
      protocolScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
         @Override
         public void handle(KeyEvent event) {
            if(KB.match(event)){
               System.out.println(prevState);
               changeScreen(prevState, mainClient);
            }
         }
      });

      primaryStage.setScene(loginScene);
      primaryStage.setResizable(false);
      primaryStage.show();
   }

   public synchronized static void changeScreen(String s, Object userData){
      if(state.equals("main")||state.equals("login")) {
         prevState = state;
      }
      state = s;
      switch(s){
         case "main":
            stage.setScene(mainScene);
            stage.setResizable(true);
            if(userData != null){
               mainClient = (ChatClient) userData;
               stage.setTitle("MultiUserChatFX Client - " + mainClient.getClientName());
            }
            notifyAllListeners("main", userData);
            break;
         case "login":
            stage.setScene(loginScene);
            stage.setResizable(false);
            notifyAllListeners("login", userData);
            break;
         case "protocol":
            stage.setScene(protocolScene);
            mainClient = (ChatClient) userData;
            stage.setResizable(true);
            notifyAllListeners("protocol", userData);
            break;
         case "users":
            notifyAllListeners("users", userData);
            break;
         case "message":
            notifyAllListeners("message", userData);
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

   public static String getPrevState(){
      return prevState;
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

   public static void setMainClient(ChatClient chatClient) {
      mainClient = chatClient;
   }
}