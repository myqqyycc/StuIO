package org.example.base;

import lombok.Data;

import java.io.InputStream;
import java.net.Socket;
import java.util.Map;

@Data
public class MyRequest {
    private InputStream inputStream;
    private Map<String, String> params;

    public MyRequest(Socket accept, Map<String, String> params) throws Exception {
        this.inputStream = accept.getInputStream();
        this.params = params;
    }

    public void read() {
        try {
            byte[] bytes = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(bytes)) > 0) {
                System.out.println(new String(bytes));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUrl() {
        return params.getOrDefault("url", "");
    }
}
