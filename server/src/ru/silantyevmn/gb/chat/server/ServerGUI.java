package ru.silantyevmn.gb.chat.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ru.silantyevmn.gb.chat.server
 * Created by Михаил Силантьев on 21.10.2017.
 */
public class ServerGUI extends JFrame implements ActionListener,Thread.UncaughtExceptionHandler,ChatServerListener{
    private final JButton jbStart=new JButton("Start");
    private final JButton jbStop=new JButton("Stop");
    private final ChatServer chatServer=new ChatServer(this);
    //private final int port=8189;
    private final int WIDTH=700;
    private final int HEIGHT=400;
    private final JTextArea log=new JTextArea();
    private final JTextField ipServer=new JTextField("localhost");
    private final JTextField port=new JTextField("8189");
    private JPanel panelServerAndPort,panelStartAndStop;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ServerGUI();
            }
        });

    }

    ServerGUI(){
        setSize(WIDTH,HEIGHT);
        setTitle("Запуск сервера");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        log.setEnabled(false);
        log.setLineWrap(true);
        JScrollPane scrollLog=new JScrollPane(log);
        add(scrollLog,BorderLayout.CENTER);

        panelStartAndStop=new JPanel();
        panelStartAndStop.setLayout(new GridLayout(1,2));
        jbStart.addActionListener(this);
        jbStop.addActionListener(this);
        panelStartAndStop.add(jbStart);
        panelStartAndStop.add(jbStop);
        add(panelStartAndStop,BorderLayout.PAGE_END);

        panelServerAndPort=new JPanel();
        panelServerAndPort.setLayout(new GridLayout(1,2));
        panelServerAndPort.add(ipServer);
        panelServerAndPort.add(port);
        add(panelServerAndPort,BorderLayout.PAGE_START);

        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj=e.getSource();
        if(obj==jbStart){
            chatServer.start(Integer.parseInt(port.getText()));
        }
        else if(obj==jbStop)chatServer.stop();
        else {
            throw new RuntimeException("Unexpected source "+obj);
        }
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        StackTraceElement[] stackTraceElements=e.getStackTrace();
        String message;
        if(stackTraceElements.length==0){
            message="Empty StackTrace";
        } else{
            message=e.getClass().getCanonicalName()+": "+
                    e.getMessage()+"\n"+
                    "\t at "+stackTraceElements[0];
        }
        JOptionPane.showMessageDialog(this,message,"Exception",JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void onChatServerLog(ChatServer chatServer, String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(message+"\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });

    }
}
