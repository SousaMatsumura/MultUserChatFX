package com.mucfx.controller;



import com.mucfx.model.Message;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import org.apache.commons.collections4.CollectionUtils;
import org.controlsfx.control.CheckListView;

import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.time.Instant;
import java.util.ArrayList;
//import java.util.Date;

public class ChatController extends Thread {

   private ChatClient client = null;
   private Message message;
   private ObservableList<String> users = FXCollections.observableArrayList();
   //private static SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss dd/MM/YY\n");

   @FXML
   private CheckListView<String> checkListViewUsers;

   @FXML
   protected TextArea textAreaMessages;

   @FXML
   protected TextField textFieldInput;

   @FXML
   protected CheckBox checkBoxAll;

   @Override
   public void run() {
      while (true) {
         sleep();
         if (client != null) {
            updateList();
            if (Main.getState().equals("close")) {
               client.logoff();
               break;
            }
         }
      }
   }

   @FXML
   protected void initialize() {
      textAreaMessages.setEditable(false);
      textFieldInput.setOnKeyPressed(e -> {
         if (e.getCode() == KeyCode.ENTER) {
            try {
               buttonSendAction(new ActionEvent());
            } catch (IOException e1) {
               e1.printStackTrace();
            }
         }
      });
      onChangeListener();
      this.setName("ChatClient");
      this.start();
   }

   @FXML
   private void onChangeListener() {
      Main.addOnChangeScreenListener(new Main.OnChangeScreen() {
         @Override
         public void onScreenChanged(String newScreen, Object userData) {
            System.out.println("newScreen: " + newScreen + " | userData: " + userData);
            if (newScreen.equals("main")) {
               if(client == null) {
                  client = (ChatClient) userData;
               }
            } else if (newScreen.equals("users")) {
               Platform.runLater(new Runnable() {
                  @Override
                  public void run() {
                     /*tempUsers.clear();
                     tempUsers.addAll((ArrayList) userData);*/
                     if(userData != null) {
                        users.clear();
                        users.addAll((ArrayList) userData);
                     }
                  }
               });
               if (client != null) {
                  System.out.println("onChangeListener: if(client != null)");
               }
            } else if (newScreen.equals("message")) {
               updateMessage((Message) userData);
            } else if (newScreen.equals("close")) {
               System.out.println("newScreen: close");
               if (client != null) {
                  try {
                     client.getServerOut().write("quit\n".getBytes());
                  } catch (IOException e) {
                     e.printStackTrace();
                  }
                  client.interrupt();
               }
            }
         }
      });
   }


   @FXML
   protected void buttonSendAction(ActionEvent event) throws IOException {

      System.out.println(textFieldInput.getText());
      String sendTo = "";
      System.out.println("Output: mensagem:" + sendTo);
      if (checkBoxAll.isSelected()) {
         for(String s : Main.getCurrentProtocol().getAllUsers()) {
            client.msg(s, String.valueOf(textFieldInput.getText()));
         }
         textFieldInput.clear();
      } else {
         for (String s : checkListViewUsers.getCheckModel().getCheckedItems()) {
            sendTo += s + Main.getCurrentProtocol().getUserSplitter();
         }
         System.out.println("Output: mensagem:" + sendTo);
         if (sendTo.length() > 0)
            sendTo = sendTo.substring(0, sendTo.length() - 1);
         System.out.println("Output: mensagem:" + sendTo);
         System.out.println("client"+client);
         client.msg(sendTo, String.valueOf(textFieldInput.getText()));
         textFieldInput.clear();
      }
   }

   @FXML
   private void updateList() {
      onChangeListener();
      System.out.println("Later size: " + users.size());
      System.out.println("client: " + client);
      System.out.println("!client.getUsers().isEmpty(): " + !client.getUsers().isEmpty());
      if (client != null && client.getUsers() != null) {
         ArrayList<String> currentUsers = new ArrayList<>();
         currentUsers.addAll(checkListViewUsers.getItems());
         ArrayList<String> newUsers = new ArrayList<>();
         newUsers.addAll(users);
         System.out.println("currentUsers: " + currentUsers);
         System.out.println("newUsers: " + newUsers);

         ArrayList<String> getOut = (ArrayList) CollectionUtils.subtract(currentUsers, newUsers);

         System.out.println("getOut:" + getOut);

         ArrayList<String> getIn = (ArrayList) CollectionUtils.subtract(newUsers, currentUsers);
         System.out.println("getIn" + getIn);
         if (!getIn.isEmpty()) {
            Platform.runLater(new Runnable() {
               @Override
               public void run() {
                  for (String s : getIn)
                     checkListViewUsers.getItems().add(s);
               }
            });
         }
         if (!getOut.isEmpty()) {
            Platform.runLater(new Runnable() {
               @Override
               public void run() {
                  for (String s : getOut)
                     checkListViewUsers.getItems().remove(s);
               }
            });
         }
      }
   }

   @FXML
   private void updateMessage(Message userData) {
      System.out.println("Inside updateMessage");
      if(message == null || !userData.equals(message)) {
         System.out.println("Inside if updateMessage");
         message = userData;
         //textAreaMessages.appendText(dateFormat.format(Date.from(Instant.now())));
         textAreaMessages.appendText(message.toString());
      }
   }

   private void sleep(){
      try {
         Thread.sleep(1000);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }
}

