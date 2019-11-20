package com.oocl.server.container;

import com.oocl.server.CustomServerApplication;
import com.oocl.server.util.ClassUtil;
import com.oocl.server.util.UrlUtil;
import lombok.Data;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

@Data
public class Container {
    private Set<Class<?>> classSet;
    private Map<String, Class<?>> parentUriClassMapping;
    private   Map<String, Method> getUri;
    private   Map<String, Method> postUri;

    private Container(){
        //todo
        final String classPath = CustomServerApplication.class.getPackage().getName();
        try {
            classSet = ClassUtil.getClasses(classPath);
            parentUriClassMapping = UrlUtil.getControllerRequestMapping(classSet);
            getUri = UrlUtil.getGetUriMethodMapping(parentUriClassMapping);
            postUri = UrlUtil.getPostUriMethodMapping(parentUriClassMapping);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Container container;

    public static Container createContainer(){
        if(container == null) container = new Container();
        return container;
    }
}
