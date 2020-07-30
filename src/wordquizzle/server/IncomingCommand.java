package wordquizzle.server;

/**
 * This class is designed to decide what to do with the incoming command from client
 */

import wordquizzle.UserState;

/**
 * This method get the cmd and redirect the program flow to the designed handler
 * @param
 */

/*public class IncomingCommand {


    public static void manageCommand(String cmd) {
        String[] tokens = cmd.split("@");
        switch (tokens[0]) {
            case "login":
                new ServerLoginHandler().manage(tokens[1]);
                break;
            case "add_friend":
                new ServerAddFriendHandler().manage(tokens[1]);
                break;
            case "logout":
                new ServerLogoutHandler().manage(tokens[1]);
            default:
                break;
        }
    }
}

*/

public abstract class IncomingCommand {

    public static long s;

    public static void manageCommand(String cmd) {
        String[] tokens = cmd.split("@");
        long s = Long.parseLong(tokens[0]);
        System.out.println(Database.getUserStatePid(Long.parseLong(tokens[0])));
        switch(Database.getUserStatePid(Long.parseLong(tokens[0]))) {
            case ONLINE:
                LoggedInMessageHandler.manageLoginCommand(tokens[1], tokens[2]);
                break;
            case OFFLINE:
                DefaultMessageHandler.manageDefaultCommand(tokens[1], tokens[2]);
        }

    }
}

class DefaultMessageHandler extends IncomingCommand {
    public static void manageDefaultCommand(String cmd, String pars) {
        switch (cmd) {
            case "login":
                new ServerLoginHandler().manage(pars);
                break;
            default:
                System.out.println("Invalid Command DEFAULT");
                break;
        }
    }
}

class LoggedInMessageHandler extends IncomingCommand {

    public static void manageLoginCommand(String cmd, String pars) {
        switch (cmd) {
            case "logout":
                new ServerLogoutHandler().manage(pars);
                break;
            case "add_friend":
                new ServerAddFriendHandler().manage(pars);
                break;
            default:
                System.out.println("Invalid Command from Login");
        }
    }
}