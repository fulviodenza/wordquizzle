package wordquizzle.client;

import wordquizzle.UserState;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * This abstract class design the handler for the outgoing command
 */
public abstract class CommandHandler {

    /**
     * This method design the correct handler to handle the outgoing command
     * @param state
     * @return
     */
    public static CommandHandler getHandler(UserState state) {

        switch(state) {
            case OFFLINE:
                return new DefaultCommandHandler();
            default:
                return new DefaultCommandHandler();
        }
    }

    /**
     * This method is designed to compute user input
     * @param msg
     */
    public void startCompute(String msg) {
        Scanner scanner = new Scanner(msg != null ? msg : "");
        compute(scanner);
        scanner.close();
    }

    public abstract void compute(Scanner scanner);
}

/**
 * This class design the handler for the outgoing command
 */
class DefaultCommandHandler extends CommandHandler {

    /**
     * This method is designed to compute user input
     * @param scanner
     */
    public void compute(Scanner scanner) {
        try {
            String cmd = scanner.next();
            switch(cmd) {
                case "register":
                    try {
                        RemoteHandlerClient rui = new RemoteHandlerClient();
                        String username = scanner.next();
                        String password = scanner.next();
                        rui.registerStub(username, password);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "quit":
                    System.exit(0);
                    break;
                case "login":
                    new LoginHandler().manage(scanner);
                    break;
                case "add_friend":
                    new AddFriendHandler().manage(scanner);
                    break;
                default:
                    System.err.println("Invalid Command");
                    break;
            }
        } catch (NoSuchElementException e) {};
    }
}
