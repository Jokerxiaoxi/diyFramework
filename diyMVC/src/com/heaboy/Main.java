package com.heaboy;

import com.heaboy.mvc.diyMVC;

public class Main {
    static {
        String path = Main.class.getResource("").getPath();      // Main类的绝对路径
        String packageName = Main.class.getPackage().getName();         // Main类的所在包
        diyMVC.scanner(path,packageName);
        System.out.println(path);           // /C:/Users/Joker/Desktop/diy/my-mvc/diyAOP/out/production/diyMVC/com/heaboy/
        System.out.println(packageName);    // com.heaboy
    }

    public static void main(String[] args) {
        diyMVC.exec("","");             // index -> index
        diyMVC.exec("test","index1");   // test -> index1
        diyMVC.exec("test","");         // test -> index
    }
}
