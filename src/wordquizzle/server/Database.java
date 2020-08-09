package wordquizzle.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import wordquizzle.UserState;
import wordquizzle.server.exceptions.UserNotFound;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class Database {

    private static volatile Database database;
    private static Path dbFile;
    private static final ConcurrentHashMap<String, User> db = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, User> onlineUsers = new ConcurrentHashMap<>();

    public static Gson createGsonBuilder() {
        return new GsonBuilder()
                .registerTypeAdapter(User.class, new User.UserJsonSerializer())
                .registerTypeAdapter(User.class, new User.UserJsonDeserializer())
                .setPrettyPrinting()
                .create();
    }

    public Database() {
        try {

            dbFile = Paths.get("../database.json");
            if(!Files.exists(dbFile)) {
                System.out.println("Server: Creating database file");
                Files.createFile(dbFile);
            }

            Gson gson = createGsonBuilder();
            Collection<User> userList = gson.fromJson(new String(Files.readAllBytes(dbFile)),
                    new TypeToken<Collection<User>>(){}.getType());
            if(userList != null) {
                userList.forEach((User user) -> db.put(user.getNick(), user));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize new database if it doesn't exists and return it
     * @return database
     */
    public static Database getDatabase() {
        if (database == null) {
            synchronized(Database.class) {
                if (database == null) {
                    database = new Database();
                    database.initFriendshipRelations();
                }
            }
        }
        return database;
    }

    /**
     * Method to initialize Friends List
     */
    private void initFriendshipRelations() {
        for (User user : db.values()) {
            user.initFriendshipRelations();
        }
    }

    /**
     * Insert the user into the database
     * @param user
     */
    public static synchronized void updateUser(User user) {

        try {
            db.put(user.getNick(), user);
            BufferedWriter writer = Files.newBufferedWriter(dbFile, StandardCharsets.UTF_8);
            writer.write(createGsonBuilder().toJson(db.values()));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param nickname
     * @return User if exists
     * @throws UserNotFound
     */
    public static User getUser(String nickname) throws UserNotFound {

        if(db.containsKey(nickname)) {
            return db.get(nickname);
        } else {
            throw new UserNotFound();
        }
    }

    /**
     *
     * @param nick
     * @return true if user is in the database, false otherwise
     */
    public static synchronized boolean containsUser(String nick) {
        try {
            getUser(nick);
            return true;
        } catch(UserNotFound e) {
            return false;
        }
    }

    public static synchronized UserState getUserStatePid(long pid) {

        if(isOnline(pid)) {
            return UserState.ONLINE;
        } else {
            return UserState.OFFLINE;
        }
    }
    /**
     * insertOnlineUsers method insert a process id to decide if a process
     * did the login
     * @param pid
     */
    public static synchronized void insertOnlineUsers(long pid, User user) {
        user.setUserState(UserState.ONLINE);
        onlineUsers.put(pid, user);
    }

    /**
     * isOnline method decide if a user is online or not
     * @param pid
     * @return true if the pid is in the onlineUsers Array, false otherwise
     */
    public static synchronized boolean isOnline(long pid) {

        return onlineUsers.containsKey(pid);
    }

    public static User getUserFromPid(long pid) throws UserNotFound {

        return getUser(onlineUsers.get(pid).getNick());
    }

    public static synchronized void removeOnlineUsers(long pid) { onlineUsers.remove(pid); }
}
