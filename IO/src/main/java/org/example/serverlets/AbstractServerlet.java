package org.example.serverlets;

import org.example.base.MyRequest;
import org.example.base.MyResponse;

public abstract class AbstractServerlet {

    public void doGet(MyRequest request, MyResponse response) throws Exception{
        doPost(request, response);
    }

    public abstract void doPost(MyRequest request, MyResponse response)throws Exception;
}
