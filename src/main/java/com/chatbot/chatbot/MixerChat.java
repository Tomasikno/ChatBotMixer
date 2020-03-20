/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chatbot.chatbot;

import com.mixer.api.MixerAPI;
import com.mixer.api.resource.MixerUser;
import com.mixer.api.resource.chat.MixerChat;
import com.mixer.api.resource.chat.events.IncomingMessageEvent;
import com.mixer.api.resource.chat.events.UserJoinEvent;
import com.mixer.api.resource.chat.methods.AuthenticateMessage;
import com.mixer.api.resource.chat.methods.ChatSendMethod;
import com.mixer.api.resource.chat.replies.AuthenticationReply;
import com.mixer.api.resource.chat.replies.ReplyHandler;
import com.mixer.api.resource.chat.ws.MixerChatConnectable;
import com.mixer.api.services.impl.ChatService;
import com.mixer.api.services.impl.UsersService;

import java.util.concurrent.ExecutionException;

/**
 *
 * @author TomPa
 */
public class MixerChat {
   
    //private MixerUser user;
   // private MixerUser chat;
   // 
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        MixerAPI mixer = new MixerAPI("bd267472ccc3eac0142b16300d3f6af1882fc56fd9cdab7a","Z26rq7sIIEMirZCxIVqaDvqcgQqu1bCiZNCoAftFq6uWGTsfs2GhyMDBinQWTbAI");
        
        MixerUser user = mixer.use(UsersService.class).getCurrent().get();
        MixerChat chat = mixer.use(ChatService.class).findOne(user.channel.id).get();
        MixerChatConnectable chatConnectable = chat.connectable(mixer);
        
        if (chatConnectable.connect()) {
           chatConnectable.send(AuthenticateMessage.from(user.channel, user, chat.authkey), new ReplyHandler<AuthenticationReply>() {
               public void onSuccess(AuthenticationReply reply) {
                   chatConnectable.send(ChatSendMethod.of("Hello World!"));
               }
               public void onFailure(Throwable var1) {
                   var1.printStackTrace();
               }
           });
        }

        chatConnectable.on(IncomingMessageEvent.class, event -> {
            if (event.data.message.message.get(0).text.startsWith("!ping")) {
                chatConnectable.send(ChatSendMethod.of(String.format("@%s PONG!",event.data.userName)));
            }
        });

        chatConnectable.on(UserJoinEvent.class, event -> {
          /*  chatConnectable.send(ChatSendMethod.of(
                    String.format("Hi %s! I'm pingbot! Write !ping and I will pong back!",
                    event.data.username)));*/
        });
    }
    
}
