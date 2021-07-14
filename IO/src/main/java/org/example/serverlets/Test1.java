package org.example.serverlets;

import org.example.base.MyRequest;
import org.example.base.MyResponse;

public class Test1 extends AbstractServerlet {
    @Override
    public void doPost(MyRequest request, MyResponse response)throws Exception {
        request.read();
    }
}
