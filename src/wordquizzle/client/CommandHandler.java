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
                case "logout":
                    new LogoutHandler().manage(scanner);
                case "add_friend":
                    new AddFriendHandler().manage(scanner);
                    break;
                case "friends_list":
                    new FriendsListHandler().manage(scanner);
                    break;
                case "help":
                    System.out.println(
                                    "usage: COMMANDS [ ARGS ...]\n" +
                                    "Commands:\n\n" +
                                    "register <user> <password>\n" +
                                    "    register user with the following password\n\n" +
                                    "login <user> <password>\n" +
                                    "    login with the user and password inserted\n\n" +
                                    "add_friend <user> <password> <userFriend>\n" +
                                    "    add the friend userFriend to the friends list of user\n" +
                                    "friends_list <user>\n" +
                                    "    print the user's friends list\n"
                    );
                    break;
                default:
                    System.err.println("Invalid Command");
                    break;
            }
        } catch (NoSuchElementException e) {};
    }
}
