package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    void register (ClientInterface client, String name) throws RemoteException;
    String getQuestion(ClientInterface client, int numQuestion) throws RemoteException;
    void sendAnswer(int numQuestion, String solution, String id) throws RemoteException;
    int finishAndGetGrade(ClientInterface client, String id) throws RemoteException;
    boolean examFinished() throws RemoteException;

}
