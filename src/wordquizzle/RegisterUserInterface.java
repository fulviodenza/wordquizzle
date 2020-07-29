package wordquizzle;

import wordquizzle.server.exceptions.AlreadyFriendsException;
import wordquizzle.server.exceptions.EmptyPassword;
import wordquizzle.server.exceptions.UserAlreadyPresent;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface RegisterUserInterface extends Remote {

    /**
     * This method register the user with the <nickname,password> couple login credentials
     * @param nickname
     * @param password
     * @return 0 if no error was encountered, 1 if the user is already present in the Database
     * @throws RemoteException
     * @throws UserAlreadyPresent
     * @throws EmptyPassword
     * @throws AlreadyFriendsException
     */
    public int register(String nickname, String password) throws RemoteException, UserAlreadyPresent, EmptyPassword, AlreadyFriendsException;
}