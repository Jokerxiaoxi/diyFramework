package com.heaboy;

import com.heaboy.annotation.Controller;
import com.heaboy.annotation.RequestMapping;

@Controller
@RequestMapping
public class IndexController {

    // 默认路由
    @RequestMapping
    public  void index(){
        System.out.println("index -> index");
    }
//    @RequestMapping
//    public  void index1(){
//        System.out.println("index -> index");
//    }
}
