package ru.silantyevmn.gb.chat.network;

import java.io.IOException;
import java.net.Socket;

/**
 * ru.silantyevmn.gb.chat.network
 * Created by Михаил Силантьев on 21.10.2017.
 */
public interface SocketThreadListener {
    void onStartSocketThread(SocketThread socketThread, Socket socket);
    void onStopSocketThread(SocketThread socketThread);
    void onSocketIsReady(SocketThread socketThread,Socket socket);
    void onReserveSring(SocketThread socketThread,Socket socket,String value);
    void onException(SocketThread socketThread,Exception e);
}
