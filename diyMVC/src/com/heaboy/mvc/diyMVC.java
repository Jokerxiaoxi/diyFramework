package com.heaboy.mvc;

import com.heaboy.annotation.Controller;
import com.heaboy.annotation.RequestMapping;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @author 章霆
 * mvc类
 */
public class diyMVC {
    // 存放每个Controller类中RequstMapping中的路径信息
    // key：Controller 类上的 @RequestMapping中的value值
    // value：Controller 方法上的 @RequestMapping中的value值
    private static HashMap<String, Map<String,Method>> map=new HashMap<>();
    // 存放Controller实例对象
    private static HashMap<String, Object> objMap=new HashMap<>();
    // （通过反射）执行Controller中对应的方法
    public static void exec(String classPath,String methodPath) {
        if (objMap.get(classPath) == null) {
            System.out.println("没有这个类 404");
        } else {
            if (map.get(classPath).get(methodPath) == null) {
                System.out.println("没有这个方法 404");
            } else {
                try {
                    // 执行Controller中对应的方法
                    map.get(classPath).get(methodPath).invoke(objMap.get(classPath));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    // 扫描初始化，扫描Main类下所有java类，挑出加了Controller注解的类，并把其中的RequestMapping中的路径放到map中
    public static void scanner(String path,String packageName) {
        List<String> paths = traverseFolder2(path);
        for (String p : paths) {
            p = p.substring(path.length() - 1);     // 类名.class
            try {
                String className = packageName + "." + p.replaceAll(Matcher.quoteReplacement(File.separator),".");
                // 包名 + 类名
                String replace = className.replace(".class", "");
                // 实例化类对象
                Class<?> cl = ClassLoader.getSystemClassLoader().loadClass(replace);
                // 判断该类是否加了Controller注解
                if (isController(cl)) {
                    // 判断该类是否加了RequestMapping注解
                    if (isRequestMapping(cl)) {
                        RequestMapping requestMapping = getRequestMapping(cl);
                        if (map.containsKey(requestMapping.value())) {
                            // 多个Controller 类上 的@RequestMapping注解都包含同一个value属性，则抛出异常
                            throw new RuntimeException("类多注解值：" + requestMapping.value());
                        } else {
                            map.put(requestMapping.value(), new HashMap<>());
                            objMap.put(requestMapping.value(), cl.newInstance());
                        }
                        // 获取Method对象
                        Method[] declaredMethods = cl.getDeclaredMethods();
                        for (Method declaredMethod : declaredMethods) {
                            // 判断Controller中的每个方法上是否加了RequestMapping注解
                            if (isRequestMapping(declaredMethod)) {
                                RequestMapping mapping = getRequestMapping(declaredMethod);
                                // 如果Controller中有多个方法的RequstMapping注解中的value值相同的话，抛出异常
                                if (map.get(requestMapping.value()).containsKey(mapping.value())) {
                                    throw new RuntimeException("方法多注解值：" + requestMapping.value());
                                } else {
                                    // 将方法上的@RequstMapping中的value值放进对应的map中
                                    map.get(requestMapping.value()).put(mapping.value(),declaredMethod);
                                }
                            }
                        }
                    }else {
                        throw  new RuntimeException("类无requestMapping");
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }


    }
    // 判断Class类是否包含自定义注解@Controller
    private static boolean isController(Class cl) {
        Annotation annotation = cl.getAnnotation(Controller.class);
        if (annotation!=null) return  true;
        return false;
    }
    // 判断Class类是否包含自定义注解@RequestMapping
    private static boolean isRequestMapping(Class cl) {
        Annotation annotation = cl.getAnnotation(RequestMapping.class);
        if (annotation!=null) return true;
        return false;
    }
    // 判断Method对象是否包含自定义注解@RequestMapping
    private static boolean isRequestMapping(Method method) {
        Annotation annotation = method.getAnnotation(RequestMapping.class);
        if(annotation!=null){
            return  true;
        }
        return false;
    }
    // 获取Class类的RequestMapping注解对象
    private static RequestMapping getRequestMapping(Class cl) {
        Annotation annotation = cl.getAnnotation(RequestMapping.class);
        if (annotation instanceof RequestMapping) return (RequestMapping) annotation;
        return null;
    }
    // 获取Class类的RequestMapping注解对象
    private static RequestMapping getRequestMapping(Method method){
        Annotation annotation = method.getAnnotation(RequestMapping.class);
        if (annotation instanceof RequestMapping) return (RequestMapping) annotation;
        return null;
    }
    // 扫描获取绝对路径path下的所有.class文件的（绝对路径/类名.class）
    private static List<String> traverseFolder2(String path) {
        File file = new File(path);
        List<String> classFiles=new ArrayList<>();
        if (file.exists()) {
            LinkedList<File> list = new LinkedList<File>();
            File[] files = file.listFiles();
            for (File file2 : files) {
                if (file2.isDirectory()) {
                    list.add(file2);
                } else {
                    classFiles.add(file2.getAbsolutePath());
                }
            }
            File temp_file;
            while (!list.isEmpty()) {
                temp_file = list.removeFirst();
                files = temp_file.listFiles();
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        list.add(file2);
                    } else {
                        classFiles.add(file2.getAbsolutePath());
                    }
                }
            }
        }
        return classFiles;
    }
}