package org.example.first;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

public class Server {
    private static Charset charset = Charset.forName("UTF-8");


    public static void main(String[] args) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress("localhost", 8080));
        serverSocketChannel.configureBlocking(false);

        Selector open = Selector.open();

        serverSocketChannel.register(open, SelectionKey.OP_ACCEPT);
        while (true) {

            int select = open.select();
            if (select == 0) {
                continue;
            }
            Iterator<SelectionKey> iterator = open.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();
                iterator.remove();
                deal(next, open);
            }

        }
    }

    public static void deal(SelectionKey selectionKey, Selector open) throws Exception {
        if (selectionKey.isAcceptable()) {
            ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
            SocketChannel accept = channel.accept();
            accept.configureBlocking(false);
            accept.register(open, SelectionKey.OP_READ);
            selectionKey.interestOps(SelectionKey.OP_ACCEPT);

            accept.write(charset.encode("你来了？"));
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (selectionKey.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            ByteBuffer allocate = ByteBuffer.allocate(1024);
            try {
                while (socketChannel.read(allocate) > 0) {
                    allocate.flip();
                    stringBuilder.append(charset.decode(allocate));
                }
                System.out.println(stringBuilder);
                selectionKey.interestOps(SelectionKey.OP_READ);
            } catch (Exception e) {
                selectionKey.cancel();
                if(selectionKey.channel()!=null){
                    selectionKey.channel().close();
                }
            }

        }


    }

}
