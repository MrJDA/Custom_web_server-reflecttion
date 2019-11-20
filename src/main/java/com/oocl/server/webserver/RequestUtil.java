package com.oocl.server.webserver;

import com.oocl.server.exception.RequestErrorException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestUtil {

    /**
     * 获取请求的相关信息
     * @param inputStream socket输入流
     * @return 返回一个请求对象
     * @throws IOException
     */
    public static  Request convertToRequest(InputStream inputStream) throws IOException {
        String requestString = getRequestString(inputStream);
        if(requestString == null || requestString.length() <=0)return null;
        Request request = new Request();
        request.setMethod(getMethod(requestString));
        request.setUri(getUri(requestString));
        request.setHost(getHost(requestString));
        request.setParameters(getParameters(requestString));
        return request;
    }

    private static String getRequestString(InputStream inputStream) throws IOException {
        StringBuilder requestBuilder = new StringBuilder();
        int requestLength = -1;
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        byte[] requestBytes = new byte[1024];
        try{
            while ((requestLength = bufferedInputStream.read(requestBytes)) != -1) {
                for (int i = 0; i < requestLength; i++) {
                    requestBuilder.append((char)requestBytes[i]);
                }
            }
        }catch(SocketTimeoutException e){}finally {
            //在还未完成socket通讯前不要将输入流关闭否则将断开流连接
            //if(inputStream != null)inputStream.close();
            //if(bufferedInputStream != null) bufferedInputStream.close();
        }
        String requestString = requestBuilder.toString();
        return requestString;
    }

    private static String getMethod(String requestString){
        int spaceIndex = requestString.indexOf(" ");
        return requestString.substring(0, spaceIndex);
    }


    private static String getUri(String requestString) {
        int spaceIndex1 = requestString.indexOf(" ");
        int spaceIndex2 = requestString.indexOf(" ", spaceIndex1+1);
        String uriWithParameter =  requestString.substring(spaceIndex1+1, spaceIndex2);
        if(uriWithParameter.indexOf("?") == -1){
            return uriWithParameter;
        }else{
            return uriWithParameter.substring(0, uriWithParameter.indexOf("?"));
        }
    }

    private static Object[] getParameters(String requestString){
        int spaceIndex1 = requestString.indexOf(" ");
        int spaceIndex2 = requestString.indexOf(" ", spaceIndex1+1);
        String uriWithParameter =  requestString.substring(spaceIndex1+1, spaceIndex2);
        if(uriWithParameter.indexOf("?") == -1)return null;
        String parameterString = uriWithParameter.substring(uriWithParameter.indexOf("?")+1);
        if(parameterString.length()<=0)return null;
        String[] parameters = parameterString.split("&");
        if(parameters.length <= 0)return null;
//        Map<String, Object> parameterMap = new LinkedHashMap<String, Object>();
//        for(int i = 0; i < parameters.length; i++){
//            String key = parameters[i].substring(0, parameters[i].indexOf("="));
//            Object value = parameters[i].substring(parameters[i].indexOf("=")+1);
//            parameterMap.put(key, value);
//        }
//        return parameterMap;
        Object[] parametersArray = new Object[parameters.length];
        for(int i = 0; i < parameters.length; i++){
            parametersArray[i] = parameters[i].substring(parameters[i].indexOf("=")+1);
        }
        return parametersArray;
    }

    private static String getHost(String requestString){
        Pattern pattern = Pattern.compile("Host: ");
        String[] keyValueArray = requestString.split("\r\n");
        int index = -1;
        for(int i = 0; i<keyValueArray.length; i++){
            Matcher matcher = pattern.matcher(keyValueArray[i]);
            if(matcher.find()){
                index = i;
                break;
            }
        }
        if(index == -1) return null;
        return keyValueArray[index].substring(keyValueArray[index].indexOf("Host: ")+6);
    }

    public static boolean isValidRequest(Request request){
        return request != null && request.getMethod() != null && request.getUri()!=null && request.getHost()!=null;
    }
}
