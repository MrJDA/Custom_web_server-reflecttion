package com.oocl.server.webserver;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SocketSever {

    private ServerSocket serverSocket;
    ExecutorService executorService;

    public SocketSever(){
        executorService = Executors.newCachedThreadPool();
        try {
            serverSocket = new ServerSocket(9090);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void socketServerStart() throws IOException {
        while (true){
            Socket socket = serverSocket.accept();
            socket.setSoTimeout(1000);
            executorService.execute(new Service(socket));
        }
    }


}
