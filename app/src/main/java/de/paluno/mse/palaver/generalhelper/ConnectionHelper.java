package de.paluno.mse.palaver.generalhelper;

import de.paluno.mse.palaver.MyApplication;
import de.paluno.mse.palaver.informationexchanger.Commando;
import de.paluno.mse.palaver.informationexchanger.Connection;
import de.paluno.mse.palaver.informationexchanger.IReceive;
import de.paluno.mse.palaver.informationexchanger.IRequest;

/**
 * Created by asus on 2017/7/5.
 */

public class ConnectionHelper implements IRequest {
    @Override
    public void login(IReceive receive, String encapsulation) {
        new Connection(receive).execute(encapsulation, Commando.VALIDATE.getPath(), Commando.VALIDATE.getCommando());

    }

    @Override
    public void signup(IReceive receive, String encapsulation) {
        new Connection(receive).execute(encapsulation, Commando.REGISTER.getPath(), Commando.REGISTER.getCommando());


    }

    @Override
    public void addfriend(IReceive receive, String encapsulation) {
        new Connection(receive).execute(encapsulation, Commando.ADDFRIENDS.getPath(), Commando.ADDFRIENDS.getCommando());

    }

    @Override
    public void getfriend(IReceive receive, String encapsulation) {
        new Connection(receive).execute(encapsulation, Commando.GETFRIENDS.getPath(), Commando.GETFRIENDS.getCommando());

    }

    /**
     * @param receive
     * @param encapsulation
     * @param extra         extra[0] =friendname
     */
    @Override
    public void removefriend(IReceive receive, String encapsulation, String[] extra) {
        new Connection(receive).execute(encapsulation, Commando.REMOVEFRIENDS.getPath(), Commando.REMOVEFRIENDS.getCommando(), extra[0]);

    }

    /**
     * @param receive
     * @param encapsulation
     * @param extra         extra[0] =recipient extra[1]=msg
     */
    @Override
    public void sendmessage(IReceive receive, String encapsulation, String[] extra) {
        new Connection(receive).execute(encapsulation, Commando.SEND.getPath(), Commando.SEND.getCommando(), extra[0],
                extra[1]);
    }

    @Override
    public void getmessage(IReceive receive, String encapsulation) {
        new Connection(receive).execute(encapsulation, Commando.GETMESSEGE.getPath(), Commando.GETMESSEGE.getCommando());

    }

    @Override
    public void getmessageoffset(IReceive receive, String encapsulation) {
        new Connection(receive).execute(encapsulation, Commando.GETOFFSET.getPath(), Commando.GETOFFSET.getCommando());

    }

    @Override
    public void password(IReceive receive, String encapsulation, String[] extra) {
        new Connection(receive).execute(encapsulation, Commando.PASSWORD.getPath(), Commando.PASSWORD.getCommando(), extra[0]);
    }


}
