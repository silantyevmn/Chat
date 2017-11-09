package ru.silantyevmn.gb.chat.server;

import ru.silantyevmn.gb.chat.library.Messages;
import ru.silantyevmn.gb.chat.network.SocketThread;
import ru.silantyevmn.gb.chat.network.SocketThreadListener;

import java.net.Socket;

/**
 * ru.silantyevmn.gb.chat.server
 * Created by Михаил Силантьев on 01.11.2017.
 */
public class ClientThread extends SocketThread {

    public ClientThread(SocketThreadListener listener, String name, Socket socket) {
        super(listener, name, socket);
    }
    private String nickname;
    private boolean isAutorized;

    public String getNickname() {
        return nickname;
    }

    public boolean isAutorized() {
        return isAutorized;
    }
    void autorizeAccept(String nickname){
        isAutorized=true;
        this.nickname=nickname;
        sendMessage(Messages.getAuthAccept(nickname));
    }

    void autorizeError(){
        isAutorized=false;
        sendMessage(Messages.getAuthDenied());
        close();
    }
    void messageError(String msg){
        sendMessage((Messages.getMsgFormatError(msg)));
        close();
    }
}
