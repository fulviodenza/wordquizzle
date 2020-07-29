package wordquizzle.server;

import wordquizzle.UserState;
import wordquizzle.server.exceptions.UserNotFound;

public abstract class ServerCommandHandler {

    public abstract void manage(String reg);
}


class ServerLoginHandler extends ServerCommandHandler {

    /**
     * The ServerLoginHandler method handle the user login. The user should be connected
     * only from one client at a time and should not be already
     * connected in the same client.
     * @param reg regular expression which contains user login credentials
     *            with the following format: "username:password"
     */
    public void manage(String reg) {

        String[] credentials = reg.split(":");
        String username = credentials[0];
        String password = credentials[1];

        password = password.replace(System.getProperty("line.separator"), "");
        User userLogin = new User(username, password);
        try {
            User user = Database.getUser(username);

            if(user.getUserState() != UserState.OFFLINE || Database.isOnline(user.getPid())) {
                System.out.println("Server: Already logged in");
                return;
            }
            if(userLogin.getPassword().equals(user.getPassword())) {
                user.setUserState(UserState.ONLINE);
                Database.insertOnlineUsers(user.getPid());
                System.out.println("Server: Logged in!");
            } else {
                System.out.println("Server: Wrong password!");
                return;
            }
        } catch(UserNotFound e) {
            e.printStackTrace();
        }
    }
}

class ServerAddFriendHandler extends ServerCommandHandler {

    /**
     * The ServerAddFriendHandler.manage handle the friendship initialization.
     * @param reg regular expression which contains user login credentials
     *            with the following format: "username:password:username"
     */
    public void manage(String reg) {

        String[] credentials = reg.split(":");
        String username = credentials[0];
        String password = credentials[1];
        String friend = credentials[2];

        User u = new User(username, password);
        try {
            User user = Database.getUser(username);
            User userFriend = Database.getUser(friend);
            //if userFriend exists and user credentials are correct
            if(userFriend != null && user.getPassword().equals(u.getPassword())) {
                user.addFriend(friend);
                System.out.println("Friend added");
            }
        } catch (UserNotFound e) {
            e.printStackTrace();
        }
    }
}