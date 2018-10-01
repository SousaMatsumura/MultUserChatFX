package com.mucfx.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mucfx.controller.Main.changeScreen;

public class LoginController {
   private ChatClient client;

   @FXML
   private TextField textFieldServerName;

   @FXML
   private TextField textFieldServerPort;

   @FXML
   private TextField textFieldClientName;

   @FXML
   private Label labelStatus;

   @FXML
   protected void initialize(){
      textFieldClientName.setText(RandomStringUtils.randomAlphabetic(8));
      textFieldClientName.setOnKeyPressed(e -> {
         if(e.getCode() == KeyCode.ENTER){
            if (String.valueOf(labelStatus.getText()).equals("Connection successful")){
               buttonLoginAction(new ActionEvent());
            }else{
               buttonConnectAction(new ActionEvent());
            }
         }
      });
      textFieldClientName.setOnKeyPressed(e -> {
         if(e.getCode() == KeyCode.ENTER){
            if (String.valueOf(labelStatus.getText()).equals("Connection successful")){
               buttonLoginAction(new ActionEvent());
            }else{
               buttonConnectAction(new ActionEvent());
            }
         }
      });
      textFieldServerName.setOnKeyPressed(e -> {
         if(e.getCode() == KeyCode.ENTER){
            if (String.valueOf(labelStatus.getText()).equals("Connection successful")){
               buttonLoginAction(new ActionEvent());
            }else{
               buttonConnectAction(new ActionEvent());
            }
         }
      });
      textFieldServerPort.setOnKeyPressed(e -> {
         if(e.getCode() == KeyCode.ENTER){
            if (String.valueOf(labelStatus.getText()).equals("Connection successful")){
               buttonLoginAction(new ActionEvent());
            }else{
               buttonConnectAction(new ActionEvent());
            }
         }
      });
   }

   @FXML
   private void onChangeListener() {
      Main.addOnChangeScreenListener(new Main.OnChangeScreen() {
         @Override
         public void onScreenChanged(String newScreen, Object userData) {
            System.out.println("newScreen: " + newScreen + " | userData: " + userData);
            if (newScreen.equals("close")) {
               if (client != null) {
                  try {
                     client.getServerOut().write("quit\n".getBytes());
                  } catch (IOException e) {
                     e.printStackTrace();
                  }
                  client.interrupt();
               }
               Thread.currentThread().interrupt();
               Platform.exit();
            }
         }
      });
   }

   @FXML
   protected void buttonConnectAction(ActionEvent e) {
      try{
         this.client = new ChatClient(String.valueOf(textFieldServerName.getText()),
            Integer.parseInt(String.valueOf(textFieldServerPort.getText())));
         if (!client.connect()){
            labelStatus.setText("Not connected");
            throw new RuntimeException("Connection failed");
         }else{
            labelStatus.setText("Connection successful");
         }
      }catch (RuntimeException ex) {
         Alert alert = new Alert(Alert.AlertType.WARNING);
         alert.setTitle("Connection failed");
         alert.setHeaderText(ex.getMessage());
         alert.setContentText(ex.getMessage());
         alert.showAndWait();
      }
   }

   @FXML
   protected void buttonLoginAction(ActionEvent e){
      try{
         if(labelStatus.getText().equals("Not connected")){
            throw new RuntimeException("Connect first");
         }else{
            client.setClientName(String.valueOf(textFieldClientName.getText()));
            if (client.login(client.getClientName()) || Main.getState().equals("logged")){
               changeScreen("main", client);
            } else {
               throw new RuntimeException("User already exists");
            }
         }
      } catch (RuntimeException ex) {
         labelStatus.setText("Not connected");
         try {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Exception");
            alert.setHeaderText(ex.getMessage());
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
         }catch (Exception exc){
            exc.printStackTrace();
         }
      } catch (IOException ex) {
         Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
}
