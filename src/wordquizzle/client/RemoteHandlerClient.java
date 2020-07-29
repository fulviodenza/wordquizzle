package wordquizzle.client;

import wordquizzle.RegisterUserInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * This class handle the remote user registration method
 */
public class RemoteHandlerClient {

    public RemoteHandlerClient() throws RemoteException {}

    /**
     * This method looks up for the UserRegisterInterface to register the
     * user with the <nickname, password> login credentials
     * @param nickname
     * @param password
     */
    public void registerStub(String nickname, String password) {
        try {
            Registry registry = LocateRegistry.getRegistry(5455);
            RegisterUserInterface stub = (RegisterUserInterface) registry.lookup("RegisterUserInterface");
            if(stub.register(nickname, password) == 0) {
                System.out.print("You have been successfully registered\n");
            } else {
                System.out.print("You're already present in the Database!\n");
            }
        } catch (Exception e)    {
            e.printStackTrace();
        }
    }
}

