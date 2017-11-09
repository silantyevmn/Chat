package ru.silantyevmn.gb.chat.server;

import ru.silantyevmn.gb.chat.library.Messages;
import ru.silantyevmn.gb.chat.network.ServerSocketThread;
import ru.silantyevmn.gb.chat.network.ServerSocketThreadListener;
import ru.silantyevmn.gb.chat.network.SocketThread;
import ru.silantyevmn.gb.chat.network.SocketThreadListener;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

/**
 * ru.silantyevmn.gb.chat.server
 * Created by Михаил Силантьев on 21.10.2017.
 */
public class ChatServer implements ServerSocketThreadListener,SocketThreadListener{
    private final DateFormat dateFormat=new SimpleDateFormat("hh:mm:ss ");
    private final ChatServerListener listener;
    private Vector<SocketThread> clients=new Vector<>();
    ServerSocketThread serverSocketThread;

    public ChatServer(ChatServerListener listener){
        this.listener=listener;
    }
    public void start(int port){
        if(serverSocketThread!=null && serverSocketThread.isAlive()){
            putLog("Не можем запустить сервер, так как он уже запущен!");
            //System.out.println("Server is already running");
        } else{
            serverSocketThread=new ServerSocketThread(this,"Server Thread",port,2000);
            SqlClient.connect();
        }

        putLog("Nick "+SqlClient.getNick("log1","123"));
    }

    public void stop(){
        if(serverSocketThread==null || !serverSocketThread.isAlive()){
            putLog("В настоящее время сервер не запущен!");
            //System.out.println("Server is not running");
        } else {
            serverSocketThread.interrupt();
            SqlClient.disconect();
        }

    }
    void putLog(String msg){
        msg=dateFormat.format(System.currentTimeMillis())+
                Thread.currentThread().getName()+":"+msg;
        listener.onChatServerLog(this,msg);
        //System.out.println(msg);
    }
/*
* события ServerSocketThread а
*
 */
    @Override
    public void onStartServerSocketThread(ServerSocketThread thread) {
        putLog("Сервер запущен");
    }

    @Override
    public void onStopServerSocketThread(ServerSocketThread thread) {
        putLog("Сервер остановлен");
    }

    @Override
    public void onCreateServerSocket(ServerSocketThread thread, ServerSocket serverSocket) {
        putLog("Создался новый сервер-сокет");
    }

    @Override
    public void onAcceptTimeout(ServerSocketThread thread, ServerSocket serverSocket) {
        //putLog("Превышено время ожидания клиента ");
    }

    @Override
    public void onSocketAccepted(ServerSocketThread thread, Socket socket) {
        putLog("Клиент подключился: "+socket);
        String name="SocketThread "+socket.getInetAddress()+" "+socket.getPort();
        new ClientThread(this,name,socket);
    }

    @Override
    public void onExceptionServerSocket(ServerSocketThread thread, Exception e) {
        putLog("Exception "+e.getClass().getName()+" "+e.getMessage());
    }
/*
* События SocketThread
 */
    @Override
    public synchronized void onStartSocketThread(SocketThread socketThread, Socket socket) {
        putLog("SocketThread стартовал");
    }

    @Override
    public synchronized void onStopSocketThread(SocketThread socketThread) {
        putLog("SocketThread остановился");
        handleAutMessage((ClientThread)socketThread," покидает чат.");
        clients.remove(socketThread);
    }

    @Override
    public synchronized void onSocketIsReady(SocketThread socketThread, Socket socket) {
        putLog("SocketThread is Ready");

        clients.add(socketThread);
    }

    @Override
    public synchronized void onReserveSring(SocketThread socketThread, Socket socket, String value) {
            ClientThread client=(ClientThread) socketThread;
            if(client.isAutorized()){
                handleAutMessage(client,value);
            } else
                handleNonAutMessage(client,value);

    }

    @Override
    public synchronized void onException(SocketThread socketThread, Exception e) {
        putLog("Exception "+e.getClass().getName()+" "+e.getMessage());
    }

    synchronized void  handleAutMessage(ClientThread client,String value){
        sendToAutorizedAccept(Messages.getTypeBrodcast(client.getNickname(),value));
    }
    synchronized void handleNonAutMessage(ClientThread client,String value){
        String[] arr=value.split(Messages.DELIMITER);
        if(arr.length!=3 || arr[0].equals(Messages.AUTH_ACCEPT)){
            client.messageError(value);
            return;
        }
        String login=arr[1];
        String pass=arr[2];
        String nickname=SqlClient.getNick(login,pass);
        if(nickname==null){
            putLog("Логин/пароль :"+login+"/"+pass+"- не верные данные!");
            client.autorizeError();
            return;
        }
        client.autorizeAccept(nickname);
        sendToAutorizedAccept(Messages.getTypeBrodcast("Сервер ",nickname+" заходит в чат."));
    }

    public synchronized void sendToAutorizedAccept(String value){
        for (int i = 0; i < clients.size(); i++) {
             ClientThread client=(ClientThread) clients.get(i);
             if(!client.isAutorized()) continue;
             client.sendMessage(value);
        }
    }
}
