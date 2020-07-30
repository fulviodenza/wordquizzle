package wordquizzle.server;

import wordquizzle.*;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;

public class UserRegister extends RemoteServer implements RegisterUserInterface {

    public UserRegister() {}

    public int register(String nickname, String password) {

        if(!Database.getDatabase().containsUser(nickname)) {
            User u = new User(nickname, password);
            Database.getDatabase().updateUser(u);
            return 0;
        } else {
            System.out.println("You're already registered!");
            return 1;
        }
    }

    public void RemoteHandler(int port) {

        try {
            UserRegister ur = new UserRegister();
            UnicastRemoteObject.exportObject(ur, port);
            Registry registry = LocateRegistry.createRegistry(5455);
            registry.bind("RegisterUserInterface", ur);
        } catch(RemoteException | AlreadyBoundException e) {
            System.err.println("Server Exception: " + e.toString());
            e.printStackTrace();
        }
    }
}

