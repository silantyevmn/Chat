package ru.silantyevmn.gb.chat.server;

import java.sql.*;

/**
 * ru.silantyevmn.gb.chat.server
 * Created by Михаил Силантьев on 01.11.2017.
 */
public class SqlClient {
    private static Connection connection=null;
    private static Statement statement;

    synchronized static void connect(){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:chatDB.db");
            statement=connection.createStatement();

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    synchronized static void disconect(){
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    synchronized static String getNick(String login,String pass){
        String request="SELECT nickname FROM users WHERE login='"+login+"' and password='"+pass+"'";
        try(ResultSet resultSet=statement.executeQuery(request)) {
            if(resultSet.next()) {
                return resultSet.getString(1).toString();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
