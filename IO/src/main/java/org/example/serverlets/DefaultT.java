package org.example.serverlets;

import org.example.base.MyRequest;
import org.example.base.MyResponse;

public class DefaultT extends AbstractServerlet {
    int i = 0;

    @Override
    public void doPost(MyRequest request, MyResponse response) throws Exception {

        System.out.println("DefaultT");
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 200 OK\n");
        builder.append("Content-Type: text/html;charset=utf-8\n\r\n")
                .append("this is DefaultT resp\r\n"+(i++)+"\r\n");
        response.write(builder.toString());
    }
}
