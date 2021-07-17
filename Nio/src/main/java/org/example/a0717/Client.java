package org.example.a0717;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;

@Data
@Accessors(chain = true)
public class Client {
    private int port = 8080;
    private Selector selector;
    private Charset charset = Charset.forName("utf-8");

    public static void main(String[] args) throws Exception {
        new Client().setPort(8080).connect();
    }

    private void connect() throws Exception {
        SocketChannel client = SocketChannel.open(new InetSocketAddress(this.port));
        client.configureBlocking(false);
        selector = Selector.open();
        client.register(selector, SelectionKey.OP_READ);
        new SendThread(client, charset).start();
        new ReceiveThread(client, selector, charset).start();
    }

    @AllArgsConstructor
    public static class SendThread extends Thread {
        SocketChannel client;
        Charset charset;

        @Override
        public void run() {
            String name = null;
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                try {
                    client.write(charset.encode(scanner.nextLine()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @AllArgsConstructor
    public static class ReceiveThread extends Thread {
        SocketChannel client;
        private Selector selector;
        Charset charset;

        @Override
        public void run() {
            while (true) {
                try {
                    while (selector.select() > 0) {
                        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                        while (iterator.hasNext()) {
                            SelectionKey next = iterator.next();
                            iterator.remove();
                            if (next.isValid() && next.isReadable()) {
                                SocketChannel client = (SocketChannel) next.channel();

                                ByteBuffer allocate = ByteBuffer.allocate(1024);
                                StringBuilder sb = new StringBuilder();
                                while (client.read(allocate) > 0) {
                                    allocate.flip();
                                    sb.append(charset.decode(allocate));
                                }
                                System.out.println(sb.toString());
                            }
                        }
                        client.register(selector, SelectionKey.OP_READ);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
