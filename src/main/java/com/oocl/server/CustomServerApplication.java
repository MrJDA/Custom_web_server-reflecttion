package com.oocl.server;


import com.oocl.server.webserver.SocketSever;

import java.io.IOException;

public class CustomServerApplication {

    public static void main(String[] args) {
        try {
            new SocketSever().socketServerStart();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
