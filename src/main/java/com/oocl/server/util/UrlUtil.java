package com.oocl.server.util;

import com.oocl.server.annotation.GetMapping;
import com.oocl.server.annotation.PostMapping;
import com.oocl.server.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class UrlUtil {
    /**
     * 遍历所有带RequestMapping注解类下的所有方法，获取请求为GET请求时，uri到对应方法的映射关系
     * @param requestMapping
     * @return
     */
    public static Map<String, Method> getGetUriMethodMapping(Map<String, Class<?>> requestMapping){
        Set<String> set = requestMapping.keySet();
        Map<String, Method> getUriMethodMap = new HashMap<String, Method>();
        for(String parentUri: set){
            Class clazz = requestMapping.get(parentUri);
            Method[] methods = clazz.getMethods();
            for(Method method: methods){
                GetMapping getMapping = method.getAnnotation(GetMapping.class);
                if(getMapping != null){
                    getUriMethodMap.put(parentUri + getMapping.getUrl(), method);
                }
            }
        }
        return getUriMethodMap;
    }

    /**
     * 遍历所有带RequestMapping注解类下的所有方法，获取请求为POST请求时，uri到对应方法的映射关系
     * @param requestMapping
     * @return
     */
    public static Map<String, Method> getPostUriMethodMapping(Map<String, Class<?>> requestMapping){
        Set<String> set = requestMapping.keySet();
        Map<String, Method> postUriMethodMap = new HashMap<String, Method>();
        for(String parentUri: set){
            Class clazz = requestMapping.get(parentUri);
            Method[] methods = clazz.getMethods();
            for(Method method: methods){
                PostMapping postMapping = method.getAnnotation(PostMapping.class);
                if(postMapping != null){
                    postUriMethodMap.put(parentUri + postMapping.postUrl(), method);
                }
            }
        }
        return postUriMethodMap;
    }

    /**
     * 获取RequestMapping中的parentUrl到对应的Class的映射关系
     * @param classSet
     * @return
     */
    public static Map<String, Class<?>> getControllerRequestMapping(Set<Class<?>> classSet){
        Map classRequestMapping = new HashMap();
        Annotation[] declaredAnnotations = null;
        for(Class<?> clazz : classSet){
            declaredAnnotations = clazz.getDeclaredAnnotations();
            if( declaredAnnotations.length > 0){
                for(Annotation annotation: declaredAnnotations){
                    if(annotation instanceof RequestMapping){
                        RequestMapping requestMapping = (RequestMapping)annotation;
                        String url = requestMapping.parentUrl();
                        classRequestMapping.put(url, clazz);
                    }
                }
            }
        }
        return  classRequestMapping;
    }
}
