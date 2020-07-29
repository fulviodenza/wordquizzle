package wordquizzle.server;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;

public class ServerConnections {

    Selector selector = Selector.open();
    SelectionKey acceptKey;
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

    public ServerConnections() throws IOException {}

    public void start(int port) {

        try {
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(port));
            acceptKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        while(!Thread.interrupted()) {
            System.out.println("Server: Waiting for requests...");
            try {
                selector.select();
                if (acceptKey.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel)acceptKey.channel();
                    SocketChannel client = server.accept();
                    if (client != null) {
                        System.out.println("Server: Accepted connection from" + client);
                        client.configureBlocking(false);
                        SelectionKey clientKey = client.register(selector, SelectionKey.OP_READ);
                        clientKey.attach(ByteBuffer.allocate(4096));
                    }
                    selector.selectedKeys().remove(acceptKey);
                }
                for(SelectionKey key : selector.selectedKeys()) {
                    if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer inBuf = (ByteBuffer)key.attachment();
                        if (client.read(inBuf) > 0) {
                            inBuf.flip();
                            System.out.printf("\n[%s]:\t%s\n", Thread.currentThread().getName(), new String(inBuf.array(), StandardCharsets.UTF_8));
                            byte[] data = new byte[inBuf.limit()];
                            inBuf.get(data, 0, inBuf.limit());
                            inBuf.clear();
                            String cmd = new String(data, StandardCharsets.UTF_8).trim();
                            IncomingCommand.manageCommand(cmd);
                        } else key.cancel();
                        //client.register(selector, SelectionKey.OP_WRITE);
                    }
                    if(key.isWritable()) {
                        //SocketChannel client = (SocketChannel) key.channel();
                        //String response = "hi - from non-blocking server";
                        //byte[] bs = response.getBytes(StandardCharsets.UTF_8);
                        //inBuf = ByteBuffer.wrap(bs);
                        //client.write(inBuf);
                        //client.register(selector, SelectionKey.OP_READ);
                    }
                    selector.selectedKeys().remove(key);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        try{
            serverSocketChannel.close();
            selector.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
