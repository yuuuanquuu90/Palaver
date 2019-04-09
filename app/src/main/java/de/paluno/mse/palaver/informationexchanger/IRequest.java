package de.paluno.mse.palaver.informationexchanger;

/**
 * Created by asus on 2017/5/18.
 */

public interface IRequest {
    void login(IReceive receive, String encapsulation);

    void signup(IReceive receive, String encapsulation);

    void addfriend(IReceive receive, String encapsulation);

    void getfriend(IReceive receive, String encapsulation);

    void removefriend(IReceive receive, String encapsulation, String[] extra);

    void sendmessage(IReceive receive, String encapsulation, String[] extra);

    void getmessage(IReceive receive, String encapsulation);

    void getmessageoffset(IReceive receive, String encapsulation);

    void password(IReceive receive, String encapsulation, String[] extra);

}
