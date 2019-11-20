package com.oocl.server.webserver;

import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
public class Request {
    private String url;
    private String method;
    private String host;
    private String uri;
    private Object[] parameters;

    public String getUrl(){
        return this.host + this.uri;
    }
}
