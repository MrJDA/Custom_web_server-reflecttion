package com.oocl.server.webserver;
import com.oocl.server.container.Container;
import com.oocl.server.exception.RequestErrorException;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;
import java.util.Set;

public class Service implements Runnable{
    private Socket socket;
    public Service(Socket socket){
        this.socket = socket;
    }
    private  void service(Request request, Response response) {
        Object responseObject = invokeMethod(request);
        response.write(responseObject, socket);
    }

    public void run() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try{
            socket.setReceiveBufferSize(1024);
            inputStream = socket.getInputStream();
            Request request = RequestUtil.convertToRequest(inputStream);
            if(!RequestUtil.isValidRequest(request))return;
            outputStream = socket.getOutputStream();
            Response response = new Response(outputStream);
            service(request, response);
        }catch (Exception ignored){
           ignored.printStackTrace();
        }finally {
           try{
               if(socket != null){
                   if(inputStream != null)inputStream.close();
                   if(outputStream != null )outputStream.close();
                   socket.close();
               }
           }catch (Exception ignored){}
        }
    }

    private static Object invokeMethod(Request request){
        Object message = null;
        Map<String, Method> uriMapping = null;
        Container container = Container.createContainer();
        if(request.getMethod().equals("GET")){
             uriMapping = container.getGetUri();
        }else if(request.getMethod().equals("POST")){
            uriMapping = container.getPostUri();
        }
        if(uriMapping == null || uriMapping.size() <= 0) try {
            throw new RequestErrorException("no such uri");
        } catch (RequestErrorException e) {
            e.printStackTrace();
        }
        Set<String> uriSet = uriMapping.keySet();
        for(String uri: uriSet){
            if(uri.equals(request.getUri())) {
                try {
                    message = uriMapping.get(uri).invoke(uriMapping.get(uri).getDeclaringClass().newInstance(), request.getParameters());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
        return message;
    }

}
