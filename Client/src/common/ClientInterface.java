package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
    void notifyExamStarted(int numQuestion) throws RemoteException;
    void examStarted() throws RemoteException;
    void examFinished() throws RemoteException;
    void notifyExamFinished(String id, int gradeFinal) throws RemoteException;
}
