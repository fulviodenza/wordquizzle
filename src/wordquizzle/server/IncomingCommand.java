package wordquizzle.server;

/**
 * This class is designed to decide what to do with the incoming command from client
 */
public class IncomingCommand {

    //TODO: Inserire sistema per non dar possibilità di effettuare operazioni mentre non si è loggati

    /**
     * This method get the cmd and redirect the program flow to the designed handler
     * @param cmd
     */
    public static void manageCommand(String cmd) {
        String[] tokens = cmd.split("@");
        switch (tokens[0]) {
            case "login":
                new ServerLoginHandler().manage(tokens[1]);
                break;
            case "add_friend":
                new ServerAddFriendHandler().manage(tokens[1]);
                break;
            default:
                break;
        }
    }
}