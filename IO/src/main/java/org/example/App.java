package org.example;

import org.example.base.MyRequest;
import org.example.base.MyResponse;
import org.example.serverlets.AbstractServerlet;
import org.example.serverlets.DefaultT;
import org.example.utils.MyUtil;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Hello world!
 */
public class App {
    Properties properties = new Properties();
    Map<String, AbstractServerlet> abstractServerletMap;
    private AbstractServerlet defaultServerlet;

    public static void main(String[] args) throws Exception {
        org.example.App app = new org.example.App();

        app.init();
        app.initAbstractServerletMap();
        app.start();
        System.out.println();
    }

    private void start() throws Exception {
        ServerSocket socket = new ServerSocket(8080);
        while (true) {
            dealReq(socket.accept());
        }

    }

    public void dealReq(Socket accept) {
        try {
            accept.setKeepAlive(true);
            accept.setSoTimeout(1000 * 60);
            MyRequest request = new MyRequest(accept, MyUtil.initMap(accept));
            MyResponse response = new MyResponse(accept);
            abstractServerletMap.getOrDefault(request.getUrl(), defaultServerlet).doGet(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void init() {
        try {

            InputStream f = this.getClass().getClassLoader().getResourceAsStream("application.properties");
            this.properties.load(f);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void initAbstractServerletMap() {
        Map<String, AbstractServerlet> map = new HashMap<>();
        try {
            for (Map.Entry entry : this.properties.entrySet()) {
                String key = String.valueOf(entry.getKey());
                if (key.startsWith("url.")) {
                    AbstractServerlet o = (AbstractServerlet) Class.forName(String.valueOf(entry.getValue())).getConstructor().newInstance();
                    map.put(key, o);
                }
            }
        } catch (Exception eE) {
            eE.printStackTrace();
        }
        this.defaultServerlet = new DefaultT();
        this.abstractServerletMap = map;
    }
}
