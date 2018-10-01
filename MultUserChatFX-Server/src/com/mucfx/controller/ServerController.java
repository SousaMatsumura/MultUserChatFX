package com.mucfx.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import static com.mucfx.controller.Main.changeScreen;


public class ServerController {

   private Exception exception = null;

   @FXML
   protected void initialize() {
      textFieldServerPort.setOnKeyPressed(e -> {
         if (e.getCode() == KeyCode.ENTER) {
            buttonUpAction(new ActionEvent());
         }
      });
   }

   private void onChangeListener() {
      Main.addOnChangeScreenListener(new Main.OnChangeScreen() {
         @Override
         public void onScreenChanged(String newScreen, Object userData) {
            if (newScreen.equals("exception")) {
               exception = (Exception) userData;
            }else if(newScreen.equals("close")){
               Thread.currentThread().interrupt();
            }
         }
      });
   }

   @FXML
   private Label labelStatus;

   @FXML
   private TextField textFieldServerPort;

   @FXML
   void buttonConfigAction(ActionEvent event) {
      changeScreen("protocol", null);
   }

   @FXML
   void buttonUpAction(ActionEvent event) {
      try {
         Server server = new Server(Integer.parseInt(String.valueOf(textFieldServerPort.getText())));
         server.start();
         labelStatus.setText("Server up");
         if (exception != null && !(exception instanceof IllegalStateException)){
            throw exception;
         }
      }catch (Exception ex) {
         Alert alert = new Alert(Alert.AlertType.WARNING);
         alert.setTitle("Server failed");
         alert.setHeaderText(ex.getMessage());
         alert.setContentText(ex.getMessage());
         alert.showAndWait();
         exception = null;
      }
   }
}
