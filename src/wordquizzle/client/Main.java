package wordquizzle.client;

import wordquizzle.UserState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

public class Main {
    public static UserState state = UserState.OFFLINE;
    public static void main(String args[]) throws IOException {
        ClientConnections cc = new ClientConnections(new InetSocketAddress("0.0.0.0",5454));
        cc.start();

        while(!Thread.interrupted()) {
            System.out.print("> ");
            InputStreamReader streamReader = new InputStreamReader(System.in);
            BufferedReader bufferedReader = new BufferedReader(streamReader);
            String username = bufferedReader.readLine();
            CommandHandler.getHandler(state).startCompute(username);
        }
    }
}