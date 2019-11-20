package com.oocl.server.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Response {

    private OutputStream outputStream;

    public Response(OutputStream outputStream){
        this.outputStream = outputStream;
    }
    public void  write(Object responseObject, Socket socket){
        PrintWriter printWriter = printWriterFactory(outputStream);
        String html = "http/1.1 200 ok\ncontent-type: text/html;charset=utf8"
                +"\n\n";
        if(responseObject == null){
            printWriter.write(html);
        }else{
            printWriter.write(html + responseObject.toString());
        }
        printWriter.flush();
        System.out.println(responseObject);
        try {
            socket.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }
        closePrintWriter(printWriter);
    }

    private PrintWriter printWriterFactory(OutputStream outputStream){
        PrintWriter printWriter = new PrintWriter(outputStream);
        return  printWriter;
    }

    private void closePrintWriter(PrintWriter printWriter){
        if(printWriter != null) printWriter.close();
    }
}
