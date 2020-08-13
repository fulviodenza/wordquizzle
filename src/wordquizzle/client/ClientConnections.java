package wordquizzle.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;

/**
 * This class handle all outgoing connections from the client
 */
public class ClientConnections extends Thread {

    private SocketChannel client;
    private static ByteBuffer tbuff;
    private static SelectionKey tcpkey;
    private static Selector selector;
    private static ByteBuffer inBuf;

    public ClientConnections(InetSocketAddress addr) {

        try {
            selector = Selector.open();
            client = SocketChannel.open();
            client.connect(addr);

            if (client.isConnected()) {
                System.out.println("Client: Connected to WordQuizzle server!");
            }

            client.configureBlocking(false);
            tcpkey = client.register(selector, SelectionKey.OP_READ);
            tbuff = ByteBuffer.allocate(4096);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * This method write data to the buffer tbuff
     * @param data
     */
    public static synchronized void write(byte[] data) {
        try {
            tcpkey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            tbuff.put(data);
        } catch (CancelledKeyException e) {
            e.printStackTrace();
        }
        selector.wakeup();
    }

    /**
     * This method write data through the socket
     * @throws IOException
     */
    private void send() throws IOException {
        if (tbuff.position() > 0) {
            tbuff.flip();
            client.write(tbuff);
            tbuff.compact();
        } else tcpkey.interestOps(SelectionKey.OP_READ);
    }

    static String read() {
        String message = null;
        if(inBuf.hasRemaining()) {
            message = String.valueOf(inBuf.get());
        }
        return message;
    }

    /**
     * This method convert data from String to Byte to write in the buffer
     * @param data
     */
    public static void write(String data) {
        write(new String(data + "\n").getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Method to handle the client selection
     */
    public void run() {
        while (!Thread.interrupted()) {
            try {
                selector.select();
                if (tcpkey.isWritable()) send();
                selector.selectedKeys().remove(tcpkey);
            } catch (Exception e) {/*do nothing */}
        }
    }
}
