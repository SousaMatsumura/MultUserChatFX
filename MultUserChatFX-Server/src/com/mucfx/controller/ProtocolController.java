
package com.mucfx.controller;

import com.mucfx.model.Protocol;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

import static com.mucfx.controller.Main.changeScreen;


public class ProtocolController {

   @FXML
   private TextField textFieldLogin;

   @FXML
   private TextField textFieldLogoff;

   @FXML
   private TextField textFieldServerToClientMessage;

   @FXML
   private TextField textFieldCommandSplitter;

   @FXML
   private TextField textFieldUserSplitter;

   @FXML
   private TextField textFieldAllUsers;

   @FXML
   private TextField textFieldClientToServerMessage;

   @FXML
   private TextField textFieldUsers;

   @FXML
   void buttonApplyAction(ActionEvent event) {
      try{
         if(textFieldLogin.getText() == null) {
            throw new RuntimeException("Field \"Login\" cannot be null.");
         }else if(textFieldLogoff.getText() == null){
            throw new RuntimeException("Field \"Logoff\" cannot be null.");
         }else if(textFieldCommandSplitter.getText() == null){
            throw new RuntimeException("Field \"Command Splitter\" cannot be null.");
         }else if(textFieldUserSplitter.getText() == null){
            throw new RuntimeException("Field \"User Splitter\" cannot be null.");
         }else if(textFieldAllUsers.getText() == null){
            throw new RuntimeException("Field \"All User\" cannot be null.");
         }else if(textFieldServerToClientMessage.getText() == null){
            throw new RuntimeException("Field \"Send Message\" cannot be null.");
         }else if(textFieldClientToServerMessage.getText() == null){
            throw new RuntimeException("Field \"Receive Message\" cannot be null.");
         }else if(textFieldUsers.getText() == null){
            throw new RuntimeException("Field \"Users\" cannot be null.");
         }else if(StringUtils.remove(String.valueOf(textFieldCommandSplitter.getText()), "\"").length() > 1){
            throw new RuntimeException("Field \"Command Splitter\" must be a character.");
         }else if(StringUtils.remove(String.valueOf(textFieldUserSplitter.getText()), "\"").length() > 1) {
            throw new RuntimeException("Field \"User Splitter\" must be a character.");
         }else{
            Protocol p = new Protocol(
               new ArrayList<String>(Arrays.asList(StringUtils.split(StringUtils.remove(String.valueOf(textFieldLogin.getText()), ";"), "\""))),
               new ArrayList<String>(Arrays.asList(StringUtils.split(StringUtils.remove(String.valueOf(textFieldLogoff.getText()), ";"), "\""))),
               StringUtils.remove(String.valueOf(textFieldCommandSplitter.getText()), "\"").charAt(0),
               StringUtils.remove(String.valueOf(textFieldUserSplitter.getText()), "\"").charAt(0),
               new ArrayList<String>(Arrays.asList(StringUtils.split(StringUtils.remove(String.valueOf(textFieldAllUsers.getText()), ";"), "\""))),
               new ArrayList<String>(Arrays.asList(StringUtils.split(StringUtils.remove(String.valueOf(textFieldServerToClientMessage.getText()), ";"), "\""))),
               new ArrayList<String>(Arrays.asList(StringUtils.split(StringUtils.remove(String.valueOf(textFieldClientToServerMessage.getText()), ";"), "\""))),
               new ArrayList<String>(Arrays.asList(StringUtils.split(StringUtils.remove(String.valueOf(textFieldUsers.getText()), ";"), "\"")))
            );
            ArrayList<String> intersection = MultiIntersection(p.getLogin(), p.getLogoff(),
               new ArrayList<String>(){{add(String.valueOf(p.getCommandSplitter()));}},
               new ArrayList<String>(){{add(String.valueOf(p.getUserSplitter()));}},
               p.getAllUsers(), p.getServerToClientMessage(),p.getClientToServerMessage(), p.getUsers());

            System.out.print("Intersection: ");
            intersection.forEach(value -> System.out.print(value));
            System.out.println(" ");
            if(!intersection.isEmpty()){
               throw new RuntimeException("Reserved words cannot be repeated. "+intersection.toString());
            }
            Main.setCurrentProtocol(p);
            System.out.println(Main.getCurrentProtocol().toString());
         }
      }catch (Exception e){
         Alert alert = new Alert(Alert.AlertType.WARNING);
         alert.setTitle("Setup failed");
         alert.setHeaderText(e.getMessage());
         alert.setContentText(e.getMessage());
         alert.showAndWait();
         e.printStackTrace();
      }

   }

   /*Curti fazer esse método HAHAHA
   * Ele serve para encontrar interssecções em multiplos conjuntos.
   * Neste caso os conjuntos são os campos que contém as palavras reservadas,
   * que são assim validados.*/

   public ArrayList<String> MultiIntersection(ArrayList<String>... args){
      ArrayList<String> list = new ArrayList<String>();
      for(int i = 0; i < args.length - 1; i++) {
         for (int j = i+1; j < args.length; j++) {
            if(!args[i].isEmpty() && !args[j].isEmpty()){
               System.out.println("~~~~~~~~~~~~~~~~~~["+i+"]"+args[i]+"---["+j+"]"+args[j]+"]~~~~~~~~~~~~~~~~~~");
               list.addAll(pairedIntersection(args[i], args[j]));
            }
         }
      }
      return list;
   }

   public ArrayList<String> pairedIntersection(ArrayList<String> a, ArrayList<String> b){
      ArrayList<String> list = new ArrayList<String>();
      for(String s : a) {
         int i = 0;
         do {
            System.out.println(" " + a + " " + " [" + a.indexOf(s) + "] " + b + " [" + i + "] " + s + " é igual a " + b.get(i) + " = " + s.equals(b.get(i)));
            if (s.equals(b.get(i))) {
               System.out.println("HERE");
               list.add(s);
            }
            i++;
         } while (i < b.size() && !b.get(i).equals(s));
         if (i < b.size()) {
            list.add(s);
            System.out.println(" " + a + " " + " [" + a.indexOf(s) + "] " + b + " [" + i + "] " + s + " é igual a " + b.get(i) + " = " + s.equals(b.get(i)));
         }
      }

      return list;
   }

   @FXML
   void buttonCancelAction(ActionEvent event) {
      changeScreen("server", null);
   }

   @FXML
   void buttonDefaultAction(ActionEvent event) {
      textFieldLogin.setText(Main.getDefaultProtocol().getProtocolLogin());
      textFieldLogoff.setText(Main.getDefaultProtocol().getProtocolLogoff());
      textFieldServerToClientMessage.setText(Main.getDefaultProtocol().getProtocolSendMessage());
      textFieldCommandSplitter.setText("\""+String.valueOf(Main.getDefaultProtocol().getCommandSplitter())+"\"");
      textFieldUserSplitter.setText("\""+String.valueOf(Main.getDefaultProtocol().getUserSplitter())+"\"");
      textFieldAllUsers.setText(Main.getDefaultProtocol().getProtocolAllUsers());
      textFieldClientToServerMessage.setText(Main.getDefaultProtocol().getProtocolReceiveMessage());
      textFieldUsers.setText(Main.getDefaultProtocol().getProtocolUsers());
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

   @FXML
   protected void initialize(){
      textFieldLogin.setText(Main.getCurrentProtocol().getProtocolLogin());
      textFieldLogoff.setText(Main.getCurrentProtocol().getProtocolLogoff());
      textFieldServerToClientMessage.setText(Main.getCurrentProtocol().getProtocolSendMessage());
      textFieldCommandSplitter.setText("\""+String.valueOf(Main.getCurrentProtocol().getCommandSplitter())+"\"");
      textFieldUserSplitter.setText("\""+String.valueOf(Main.getCurrentProtocol().getUserSplitter())+"\"");
      textFieldAllUsers.setText(Main.getCurrentProtocol().getProtocolAllUsers());
      textFieldClientToServerMessage.setText(Main.getCurrentProtocol().getProtocolReceiveMessage());
      textFieldUsers.setText(Main.getCurrentProtocol().getProtocolUsers());
   }


}