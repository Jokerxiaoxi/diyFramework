package com.heaboy.Controller;

import com.heaboy.annotation.Controller;
import com.heaboy.annotation.RequestMapping;

@Controller
@RequestMapping("test")
public class TestController {
    @RequestMapping
    public  String index(){
        System.out.println("test -> index");
        return "";
    }
    @RequestMapping("index1")
    public  String index1(){
        System.out.println("test -> index1");
        return "";
    }
}
