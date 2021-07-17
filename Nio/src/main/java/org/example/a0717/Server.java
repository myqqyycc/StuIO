package org.example.a0717;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Data
@Accessors(chain = true)
public class Server {
    private int port = 8080;
    private Selector selector;
    private Charset charset = Charset.forName("utf-8");

    public static void main(String[] args) throws Exception {
        new Server().setPort(8080).listen();
    }

    public void listen() throws Exception {
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.socket().bind(new InetSocketAddress(this.port));
        channel.configureBlocking(false);

        selector = Selector.open();

        channel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            while (selector.select() > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey next = iterator.next();
                    iterator.remove();
                    dealClient(next);
                }
            }
        }
    }

    Map<SocketChannel, String> onLineCLientNameMap = new HashMap<>();

    Map<String, SocketChannel> onLineCLientMap = new HashMap<>();

    private void dealClient(SelectionKey key) throws Exception {
        if (!key.isValid()) {
            return;
        }

        if (key.isAcceptable()) {
            ServerSocketChannel waitClient = (ServerSocketChannel) key.channel();


            SocketChannel accept = waitClient.accept();
            accept.configureBlocking(false);

            accept.write(charset.encode("老弟你来了？whatis your name?"));
            accept.register(selector, SelectionKey.OP_READ);
            return;
        }
        if (key.isReadable()) {
            SocketChannel client = null;
            try {
                client = (SocketChannel) key.channel();
                ByteBuffer allocate = ByteBuffer.allocate(1024);
                StringBuilder sb = new StringBuilder();
                while (client.read(allocate) > 0) {
                    allocate.flip();
                    sb.append(charset.decode(allocate));
                }
                System.out.println(sb.toString());


                if (!onLineCLientNameMap.containsKey(client)) {
                    int clientType = clientType(sb.toString(), client);
                    switch (clientType) {
                        case 0:
                            client.write(charset.encode(sb.toString() + " 老弟，你换个马甲吧，名字已存在哩！"));

                            break;
                        case 1:
                            client.write(charset.encode(" 恭喜上线成果！"));
                            onLineCLientNameMap.put(client, sb.toString());
                            sendToOther(key, sb.toString() + " 我上线了！！！");
                            break;
                        case 2:
                            client.write(charset.encode(" 你他吗成功挤掉了之前的用户！"));
                            onLineCLientNameMap.put(client, sb.toString());
                            sendToOther(key, sb.toString() + " 我暴力上线，之前的被挤掉了了！！！");
                            break;
                        default:
                            client.write(charset.encode(" 天知道，发生了什么！"));
                    }
                } else {
                    sendToOther(key, sb.toString());
                }


                client.register(selector, SelectionKey.OP_READ);
            } catch (Exception e) {
                if (client != null) {
                    client.close();
                }
            }

        }

    }

    /**
     * 0 无效（已被占用）
     * 1 上线成果
     * 2 挤掉无效用户
     *
     * @param name
     * @param client
     * @return
     */
    private int clientType(String name, SocketChannel client) {
        SocketChannel put = onLineCLientMap.put(name, client);

        if (put == null || put.equals(client)) {
            return 1;
        } else if (selector.keys().stream().anyMatch(v -> v.channel() == put)) {
            onLineCLientMap.put(name, put);
            return 0;
        } else {
            return 2;
        }
    }

    private void sendToOther(SelectionKey key, String content) {
        SelectableChannel user = key.channel();
        String name = onLineCLientNameMap.getOrDefault(user, "佚名");
        selector.keys().forEach(v -> {
            SocketChannel channel;
            if (v.isValid() && v.channel() instanceof SocketChannel) {
                channel = (SocketChannel) v.channel();
                if (user != channel) {
                    try {
                        channel.write(charset.encode(name + "说：" + content));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else {
                System.out.println("跳过发送："+v);
            }
        });
    }
}
