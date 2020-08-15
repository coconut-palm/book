package com.atguigu.utils;

import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class WebUtils {

    public static <T> T copyParamToBean(Map value, T bean){
        try {
            System.out.println("注入前"+bean);
            //把所有请求的参数都注入到user对象中
            BeanUtils.populate(bean,value);
            System.out.println("注入后"+bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }
}
