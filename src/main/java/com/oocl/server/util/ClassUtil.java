package com.oocl.server.util;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassUtil {

    /**
     * 根据包名获取该包及其子包中所有的类的CLass对象的集合
     * @param packageName 包名称
     * @return 返回所有Class的集合
     * @throws IOException
     */
    public static Set<Class<?>> getClasses(String packageName) throws IOException {
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".", "/"));
        while(urls.hasMoreElements()){
            URL url = urls.nextElement();
            if(url != null){
                String protocol = url.getProtocol();
                if(protocol.equals("file")){
                    //去除文件路径中可能存在的%20
                    String packagePath = url.getPath().replaceAll("%20", " ");
                    addClass(classSet, packagePath, packageName);
                }else if(protocol.equals("jar")){
                    JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                    if(jarURLConnection != null){
                        JarFile jarFile = jarURLConnection.getJarFile();
                        if(jarFile != null){
                            Enumeration<JarEntry> entries = jarFile.entries();
                            while(entries.hasMoreElements()){
                                JarEntry jarEntry = entries.nextElement();
                                String jarEntryName = jarEntry.getName();
                                if(jarEntryName.endsWith(".class")){
                                    String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                                    doAddClass(classSet, className);
                                }
                            }
                        }
                    }
                }
            }
        }
        return classSet;
    }

    private static void doAddClass(Set<Class<?>> classSet, String className) {
        Class<?> clazz = loadClass(className);
        classSet.add(clazz);
    }

    private static Class<?> loadClass(String className) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className, true, getClassLoader());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
    }

    private static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();

    }

    /**
     * 将包及子包中的类通过包路径+类名的方式添加到集合中去
     * @param classSet
     * @param packagePath
     * @param packageName
     */
    private static void addClass(Set<Class<?>> classSet, String packagePath, String packageName) {
        //用以过滤出类
        File[] files  = new File(packagePath).listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (file.isFile() && file.getName().endsWith(".class") || file.isDirectory());
            }
        });
        if (files != null) {
            for(File file: files){
                String fileName = file.getName();
                if(file.isFile()){
                    String className = fileName.substring(0, fileName.lastIndexOf("."));
                    if(packageName != null && packageName.length() > 0){
                        className = packageName + "." + className;
                        doAddClass(classSet, className);
                    }
                }else{   //递归遍历包总的子包
                    String subPackagePath = fileName;
                    if(packagePath.length() > 0){
                        subPackagePath = packagePath + "/" + subPackagePath;
                    }
                    String subPackageName = fileName;
                    if(packageName != null && packageName.length() > 0){
                        subPackageName = packageName + "."  + subPackageName;
                    }
                    addClass(classSet, subPackagePath, subPackageName);
                }
            }
        }
    }
}
