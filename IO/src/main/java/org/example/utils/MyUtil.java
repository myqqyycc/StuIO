package org.example.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MyUtil {

    public static Map<String, String> initMap(Socket accept) {
        String content = initContent(accept);

        String[] lines = StringUtils.split(content, "\r\n");
        Map<String, String> map = new HashMap(lines.length);
        for (String s1 : lines) {
            if (StringUtils.isWhitespace(s1)) {
                continue;
            }
            int index = s1.indexOf(":");
            index = index > -1 ? index : s1.indexOf(" ");
            if (index != -1) {
                map.put(s1.substring(0, index), s1.substring(index + 1));
            }
        }
        map.put("METHOD", map.containsKey("GET") ? "GET" : "POST");
        String url = map.get(map.get("METHOD"));
        map.put("URL", url.substring(1, url.indexOf(" ")));
        return map;
    }

    public static String initContent(Socket socket) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[4096];
            int length;
            if ((length = inputStream.read(bytes)) > 0) {
                String s = new String(bytes);
                sb.append(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
