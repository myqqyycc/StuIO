package org.example.first;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;

public class Client {
    private static Charset charset = Charset.forName("UTF-8");

    public static void main(String[] args) throws Exception {

        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 8080));

        socketChannel.configureBlocking(false);

        Selector open = Selector.open();

        socketChannel.register(open, SelectionKey.OP_READ);

        new Thread(() -> {
            while (true) {
                int select = 0;
                try {
                    select = open.select();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (select == 0) {
                    continue;
                }
                Iterator<SelectionKey> iterator = open.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey next = iterator.next();
                    iterator.remove();
                    dealRead(next, open);
                }
            }
        }).start();

        new Thread(() -> {
            dealWrite(socketChannel);
        }).start();


    }

    public static void dealRead(SelectionKey selectionKey, Selector open) {
        try {
            if (selectionKey.isReadable()) {
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                ByteBuffer allocate = ByteBuffer.allocate(1024);
                StringBuilder stringBuilder = new StringBuilder();
                while (socketChannel.read(allocate) > 0) {
                    allocate.flip();
                    stringBuilder.append(charset.decode(allocate));
                }
                System.out.println(stringBuilder);
                selectionKey.interestOps(SelectionKey.OP_READ);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void dealWrite(SocketChannel socketChannel) {
        try {
            Scanner scanner = new Scanner(System.in);

            while (scanner.hasNextLine()) {
                String s = scanner.nextLine();
                socketChannel.write(charset.encode(s));
            }
            System.out.println("sdfsdaaf");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }


    }


}
