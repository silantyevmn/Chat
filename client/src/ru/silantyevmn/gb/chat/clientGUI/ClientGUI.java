package ru.silantyevmn.gb.chat.clientGUI;

import com.sun.xml.internal.ws.server.ServerRtException;
import ru.silantyevmn.gb.chat.library.Messages;
import ru.silantyevmn.gb.chat.network.SocketThread;
import ru.silantyevmn.gb.chat.network.SocketThreadListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * ru.silantyevmn.gb.chat.clientGUI
 * Created by Михаил Силантьев on 22.10.2017.
 */
public class ClientGUI extends JFrame implements ActionListener, SocketThreadListener, Thread.UncaughtExceptionHandler {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientGUI();
            }
        });
    }

    private final String WINDOW_TITLE="Чат";
    private final DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss ");
    private SocketThread socketThread;
    private static final int WIDTH = 700;
    private static final int HEIGHT = 500;
    private final JTextArea log = new JTextArea();
    private final JTextField tfLogin = new JTextField();
    private final JPasswordField tfPass = new JPasswordField();
    private final JButton bLogin = new JButton("Логин");
    private JTextField tfIpAdress = new JTextField("localhost");
    private JTextField tfPort = new JTextField("8189");
    private JCheckBox cbAlwaysOnTop = new JCheckBox("123");
    private JPanel panelTop, panelIpAndPort, panelDown;
    private final JButton bDisconect = new JButton("Отключится");
    private final JTextField tfmessage = new JTextField();
    private final JButton bSend = new JButton("Отправить");

    ClientGUI() {
        setSize(WIDTH, HEIGHT);
        setTitle(WINDOW_TITLE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        panelTop = new JPanel();
        panelTop.setLayout(new GridLayout(2, 3));
        panelTop.add(tfIpAdress);
        panelTop.add(tfPort);
        panelTop.add(cbAlwaysOnTop);
        panelTop.add(tfLogin);
        panelTop.add(tfPass);
        panelTop.add(bLogin);
        tfLogin.addActionListener(this);
        tfPass.addActionListener(this);
        bLogin.addActionListener(this);
        add(panelTop, BorderLayout.PAGE_START);

        JScrollPane scrollLog = new JScrollPane(log);
        log.setEnabled(false);
        log.setLineWrap(true);
        add(scrollLog, BorderLayout.CENTER);

        panelDown = new JPanel();
        panelDown.setLayout(new GridLayout(1, 3));
        tfmessage.setPreferredSize(new Dimension(500, 30));
        tfmessage.addActionListener(this);
        bSend.addActionListener(this);
        bDisconect.addActionListener(this);
        panelDown.add(bDisconect);
        panelDown.add(tfmessage);
        panelDown.add(bSend);
        add(panelDown, BorderLayout.PAGE_END);

        setVisible(true);


    }

    @Override
    public synchronized void onStartSocketThread(SocketThread socketThread, Socket socket) {
        putLog("Поток сокета стартовал");
    }

    @Override
    public synchronized void onStopSocketThread(SocketThread socketThread) {
        putLog("Соединение разорвано");
        panelTop.setVisible(true);
        panelDown.setVisible(false);
    }

    @Override
    public synchronized void onSocketIsReady(SocketThread socketThread, Socket socket) {
        putLog("Соединение установлено");
        String login = tfLogin.getText();
        String pass = new String(tfPass.getPassword());
        socketThread.sendMessage(Messages.getAuthRequest(login, pass));
        panelTop.setVisible(false);
        panelDown.setVisible(true);
    }

    @Override
    public synchronized void onReserveSring(SocketThread socketThread, Socket socket, String value) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //log.append(value + "\n");
                handleMessage(value);
            }
        });
    }

    private synchronized void handleMessage(String value) {
        String[] arr=value.split(Messages.DELIMITER);
        String typeMessage=arr[0];
        switch (typeMessage){
            case Messages.AUTH_ACCEPT: {
                putLog("Авторизация прошла успешно!\nДобро пожаловать: "+arr[1]);
                setTitle(WINDOW_TITLE+" вход под ником "+arr[1]);
                break;
            }case Messages.AUTH_DENIED:{
                putLog("Авторизация не прошла! \n"+value);
                break;
            }case Messages.MSG_FORMAT_ERROR:{
                putLog("Ошибка в сообщении!\n"+value);
                socketThread.close();
                break;
            }case Messages.TYPE_BRODCAST:{
                putLog(dateFormat.format(Long.parseLong(arr[1]))+" "+arr[2]+":"+arr[3]);
                break;
            }
        }
    }

    @Override
    public synchronized void onException(SocketThread socketThread, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void uncaughtException (Thread t, Throwable e) {
        e.printStackTrace();
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        String message;
        if (stackTraceElements.length == 0) {
            message = "Empty StackTrace";
        } else {
            message = e.getClass().getCanonicalName() + ": " +
                    e.getMessage() + "\n" +
                    "\t at " + stackTraceElements[0];
        }
        JOptionPane.showMessageDialog(this, message, "Exception", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj == tfmessage || obj == bSend) {
            sendMessage();
        } else if (obj == tfLogin || obj == tfPass || obj == tfIpAdress || obj == tfPort || obj == bLogin) {
            connect();
        } else if (obj == bDisconect) {
            socketThread.close();
        }
//        else if(obj==cbAlwaysOnTop){
//            setAlwaysOnTop(cbAlwaysOnTop.isSelected());
//        }
        else {
            throw new RuntimeException("Unknow source: " + obj);
        }
    }

    private void connect() {
        Socket socket = null;
        try {
            socket = new Socket(tfIpAdress.getText(), Integer.parseInt(tfPort.getText()));
        } catch (IOException e) {
            log.append("Exception: " + e.getMessage());

        }
        socketThread = new SocketThread(this, "SocketThread", socket);


    }

    synchronized void putLog(String msg) {
        //msg = dateFormat.format(System.currentTimeMillis()) + msg;
                //Thread.currentThread().getName() + ":" + msg;
        log.append(msg + "\n");
        System.out.println(msg);
    }

    synchronized void sendMessage() {
        String msg = tfmessage.getText();
        String username = tfLogin.getText();
        if ("".equals(msg)) return;
        //log.append(username+": "+msg+"\n");
        tfmessage.setText(null);
        tfmessage.requestFocusInWindow();
        socketThread.sendMessage(msg);

    }
}
