package wordquizzle.client;

import java.util.Scanner;

/**
 * This abstract class represent the architectural model for a single Command
 */
public abstract class CLICommand {

    public abstract void manage(Scanner scanner);
}

/**
 * This class is used to send the login command with the format login@user:password
 */
class LoginHandler extends CLICommand {

    /**
     * This method takes as input the user input and sends to server the common command string
     * @param scanner
     */
    public void manage(Scanner scanner) {

        String username = scanner.next();
        String password = scanner.next();
        String pid = Long.toString(ProcessHandle.current().pid());
        ClientConnections.write(pid+"@login@"+username+":"+password);
    }
}

/**
 * This class is used to send the add_friend command with the format add_friend@user:password:user
 */
class AddFriendHandler extends CLICommand {

    /**
     * This method takes as input the user input and sends to server the common command string
     * @param scanner
     */
    public void manage(Scanner scanner) {

        String username = scanner.next();
        String password = scanner.next();
        String usernameFriend = scanner.next();
        String pid = Long.toString(ProcessHandle.current().pid());
        ClientConnections.write(pid+"@add_friend@"+username+":"+password+":"+usernameFriend);
    }
}

class LogoutHandler extends CLICommand {

    public void manage(Scanner scanner) {

        String username = scanner.next();
        String pid = Long.toString(ProcessHandle.current().pid());
        ClientConnections.write(pid+"@"+"logout@"+username);
    }
}

class FriendsListHandler extends CLICommand {

    public void manage(Scanner scanner) {

        String username = scanner.next();
        String pid = Long.toString(ProcessHandle.current().pid());
        ClientConnections.write(pid+"@"+"friends_list@"+username);
    }
}