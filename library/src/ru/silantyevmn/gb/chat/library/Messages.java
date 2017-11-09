package ru.silantyevmn.gb.chat.library;

/**
 * ru.silantyevmn.gb.chat.library
 * Created by Михаил Силантьев on 01.11.2017.
 */
public class Messages {
    /*
    /auth_request&login&password авторизация логин пароль
    /auth_ok&nickname авторизация прошла успешно
    /auth_denied авторизация не прошла

    /msg_format_error
    /type_brodcast  разослать всем
    /userlist&user1&user2
     */

    public static final String DELIMITER="&";
    public static final String AUTH_REQUEST="/auth_request";
    public static final String AUTH_ACCEPT="/auth_accept";
    public static final String AUTH_DENIED="/auth_denied";
    public static final String MSG_FORMAT_ERROR="/msg_format_error";
    public static final String TYPE_BRODCAST="/type_brodcast";
    public static final String USER_LIST="/userlist";

    public static String getUserList(String users){
        return USER_LIST+DELIMITER+users;
    }

    public static String getAuthRequest(String login,String pass){
        return AUTH_REQUEST+DELIMITER+login+DELIMITER+pass;
    }

    public static String getAuthAccept(String nickname){
        return AUTH_ACCEPT+DELIMITER+nickname;
    }

    public static String getAuthDenied(){
        return AUTH_DENIED;
    }

    public static String getMsgFormatError(String message){
        return MSG_FORMAT_ERROR+DELIMITER+message;
    }

    public static String getTypeBrodcast(String src,String message){
        return TYPE_BRODCAST+DELIMITER+System.currentTimeMillis()+DELIMITER+
                src+DELIMITER+message;
    }
}
