package ru.silantyevmn.gb.chat.network;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * ru.silantyevmn.gb.chat.network
 * Created by Михаил Силантьев on 21.10.2017.
 */
public interface ServerSocketThreadListener {
    void onStartServerSocketThread(ServerSocketThread thread);
    void onStopServerSocketThread(ServerSocketThread thread);

    void onCreateServerSocket(ServerSocketThread thread, ServerSocket serverSocket);
    void onAcceptTimeout(ServerSocketThread thread,ServerSocket serverSocket);

    void onSocketAccepted(ServerSocketThread thread,Socket socket);
    void onExceptionServerSocket(ServerSocketThread thread, Exception e);


}
