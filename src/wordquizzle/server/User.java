package wordquizzle.server;

import com.google.gson.*;
import wordquizzle.UserState;
import wordquizzle.server.exceptions.AlreadyFriendsException;
import wordquizzle.server.exceptions.UserNotFound;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class User {

    public void initFriendshipRelations() {
        synchronized (friendsList) {
            for(User user : friendsList.values()) {
                try {
                    User friend = Database.getUser(user.getNick());
                    if(friendsList.remove(friend.getNick(), this)) throw new AlreadyFriendsException();
                    friendsList.put(friend.getNick(), friend);
                } catch (UserNotFound | AlreadyFriendsException e) {}
            }
        }
    }

    public static class UserJsonSerializer implements JsonSerializer<User> {

        public JsonElement serialize(User user, Type src, JsonSerializationContext context) {

            JsonObject obj = new JsonObject();
            obj.add("username", new JsonPrimitive(user.getNick()));
            obj.add("password", new JsonPrimitive(user.getPassword()));
            JsonArray friendsList = new JsonArray();
            for (User u : user.getFriendsList().values()) {
                friendsList.add(u.getNick());
            }
            obj.add("friendsList", friendsList);
            return obj;
        }
    }

    public static class UserJsonDeserializer implements JsonDeserializer<User> {

        public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            JsonArray jsonFriendsList = obj.getAsJsonArray("friendsList");
            ArrayList<User> friendsList = new ArrayList<>();

            for(JsonElement e : jsonFriendsList) {
                User u = new User();
                u.setNick(e.getAsString());
                friendsList.add(u);
            }

            return new User(
                    obj.getAsJsonPrimitive("username").getAsString(),
                    obj.getAsJsonPrimitive("password").getAsString()
            );
        }
    }

    String nick;
    String password;
    private final ConcurrentHashMap<String, User> friendsList;
    private UserState state = UserState.OFFLINE;

    /**
     * Creates a new User with nick and password
     * information for the login
     * @param nick User nick
     * @param password User password
     */

    public User(String nick, String password) {
        this.nick = nick;
        this.password = password;
        this.friendsList = new ConcurrentHashMap<>();
    }

    public User() {
        this.friendsList = new ConcurrentHashMap<>();
    }

    /**
     * Add a new friend with the nick nickF
     * to the FriendsList
     * @param username friend nick to add
     */
    public void addFriend(String username) {

        try{
            User friend = Database.getUser(username);
            friendsList.put(username, friend);
        } catch(UserNotFound e) {
            e.printStackTrace();
        }

    }

    /*
    GETTER METHODS
     */
    public UserState getUserState() {
        return this.state;
    }
    /**
     * @return the User nick
     */
    public String getNick() {
        return nick;
    }

    public String getPassword() {return password;}

    public ConcurrentHashMap<String, User> getFriendsList() {
        return this.friendsList;
    }

    public long getPid() {
        return ProcessHandle.current().pid();
    }

    /*
    SETTER METHODS
     */
    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserState(UserState state) {
        synchronized (this.state) {
            this.state = state;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return password.equals(user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(password);
    }
}

