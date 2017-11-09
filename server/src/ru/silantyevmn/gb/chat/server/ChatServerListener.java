package ru.silantyevmn.gb.chat.server;

/**
 * ru.silantyevmn.gb.chat.server
 * Created by Михаил Силантьев on 01.11.2017.
 */
public interface ChatServerListener {
    void onChatServerLog(ChatServer chatServer,String message);
}
