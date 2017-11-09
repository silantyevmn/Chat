package ru.silantyevmn.gb.chat.network;

import java.io.IOException;
import java.net.*;


/**
 * ru.silantyevmn.gb.chat.network
 * Created by Михаил Силантьев on 21.10.2017.
 */
public class ServerSocketThread extends Thread{
    private ServerSocketThreadListener listener;
    private final int port;
    private final int timeout;

    @Override
    public void run() {
        listener.onStartServerSocketThread(this);//запущен поток
        try {
            ServerSocket serverSocket=new ServerSocket(port);
            serverSocket.setSoTimeout(timeout);
            listener.onCreateServerSocket(this,serverSocket);//стартовал сервер
            Socket socket;
            while (!isInterrupted()){
                try {
                    socket=serverSocket.accept();
                } catch (SocketTimeoutException e){
                    listener.onAcceptTimeout(this,serverSocket);//ошибка тайм аута
                    //System.out.println("Случился timeout exception");
                    continue;
                }
                listener.onSocketAccepted(this,socket);
                //System.out.println("Создался новый socket");
            }
        } catch (IOException e) {
            listener.onExceptionServerSocket(this,e);
            //e.printStackTrace();
        } finally {
            listener.onStopServerSocketThread(this);
            //System.out.println("Поток остановился");
        }
    }

    public ServerSocketThread(ServerSocketThreadListener listener, String name, int port, int timeout){
        super(name);
        this.port=port;
        this.timeout=timeout;
        this.listener=listener;
        start();
    }
}
