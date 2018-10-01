package com.mucfx.model;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public class Message {
   private String from;
   private String[] sendTo;
   private String msgBody;

   public Message(String from, String[] sendTo, String msgBody) {
      this.from = from;
      this.sendTo = sendTo;
      this.msgBody = msgBody;
   }

   private String writeSendTo(){
      String to = "";
      for (String s : sendTo) {
         to += s + ", ";
      }
      to = to.substring(0, to.length() - 2);
      return to;
   }

   @Override
   public String toString() {
      return from+" said to ["+ (sendTo[0].equals("*")? "ALL":writeSendTo()) +"] :\n   "+msgBody+"\n";
   }
}
