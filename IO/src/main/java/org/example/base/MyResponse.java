package org.example.base;

import java.io.OutputStream;
import java.net.Socket;

public class MyResponse {
    private OutputStream outputStream;

    public MyResponse(Socket socket) throws Exception {
        this.outputStream = socket.getOutputStream();
    }

    public void write(String result) {
        try {
            outputStream.write(result.getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
