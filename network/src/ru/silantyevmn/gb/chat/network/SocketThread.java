package ru.silantyevmn.gb.chat.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.Socket;

/**
 * ru.silantyevmn.gb.chat.network
 * Created by Михаил Силантьев on 21.10.2017.
 */
public class SocketThread extends Thread{
    private Socket socket;
    private SocketThreadListener listener;
    private DataOutputStream out;

    public SocketThread(SocketThreadListener listener, String name, Socket socket){
        super(name);
        this.socket=socket;
        this.listener=listener;
        start();
    }

    @Override
    public void run() {
        try {
            listener.onStartSocketThread(this,socket);
            DataInputStream in=new DataInputStream(socket.getInputStream());
            out=new DataOutputStream(socket.getOutputStream());
            listener.onSocketIsReady(this,socket);
            while (!isInterrupted()){
                String msg=in.readUTF();
                listener.onReserveSring(this,socket,msg);
            }
        }catch (IOException e){
            listener.onException(this,e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                listener.onException(this,e);
            }
            listener.onStopSocketThread(this);
        }
    }

    public synchronized boolean sendMessage(String msg){
        try {
            out.writeUTF(msg);
            out.flush();
            return true;
        } catch (IOException e) {
            listener.onException(this,e);
            close();
            //e.printStackTrace();
            return false;
        }
    }

    public synchronized void close(){
        interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            listener.onException(this,e);
        }
    }
}
