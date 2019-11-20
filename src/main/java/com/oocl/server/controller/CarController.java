package com.oocl.server.controller;


import com.oocl.server.annotation.GetMapping;
import com.oocl.server.annotation.RequestMapping;

//添加requestMapping注解，以供扫描包下所有带该注解的类，来模拟获取RequestMapping对应的url
@RequestMapping(parentUrl = "/car")
public class CarController {

    // 添加getMapping注解，通过getUrl可以映射到getCarBrand方法，然后用以反射调用
    @GetMapping(getUrl = "/getCarBrand")
    public String getCarBrand(){
        return "奔驰";
    }
}
