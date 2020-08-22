package com.atguigu.web;

import com.atguigu.pojo.User;
import com.atguigu.service.UserService;
import com.atguigu.service.impl.UserServiceImpl;
import com.atguigu.utils.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

import static com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY;

public class UserServlet extends BaseServlet{
    private UserService userService = new UserServiceImpl();

    //注销
    protected void logout(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.销毁session中用户登录的信息
        req.getSession().invalidate();
        //2.重定向到首页
        resp.sendRedirect(req.getContextPath());
    }

    protected void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.获取请求的参数
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        //2.调用userService.login()登录处理业务
        User loginUser = userService.login(new User(null, username, password, null));

        //如果等于null 说明登录失败
        if (loginUser == null) {
            //把错误信息和回显的表单项信息 保存到request域中
            req.setAttribute("msg", "用户名或密码错误");
            req.setAttribute("username", username);

            //跳回登录页面
            req.getRequestDispatcher("/pages/user/login.jsp").forward(req, resp);
        } else {
            //登录成功
            //保存用户登录的信息到session域中
            req.getSession().setAttribute("user",loginUser);

            req.getRequestDispatcher("/pages/user/login_success.jsp").forward(req, resp);
        }
    }

    protected void regist(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取session中的验证码
        String token = (String) req.getSession().getAttribute(KAPTCHA_SESSION_KEY);
        //删除session中的验证码
        req.getSession().removeAttribute(KAPTCHA_SESSION_KEY);

        //1.获取请求的参数
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String email = req.getParameter("email");
        String code = req.getParameter("code");

//        User user = new User();
//        WebUtils.copyParamToBean(req,user);

        User user = WebUtils.copyParamToBean(req.getParameterMap(), new User());

        //2.检查验证码是否正确
        if (token != null && token.equalsIgnoreCase(code)) {
            //3.检查用户名是否可用
            if (userService.existUsername(username)) {
                //不可用
                System.out.println("用户名" + username + "已存在");
                //把回显信息 保存到request域中
                req.setAttribute("msg", "用户名已存在!");
                req.setAttribute("username", username);
                req.setAttribute("email", email);
                req.getRequestDispatcher("/pages/user/regist.jsp").forward(req, resp);

            } else {
                //可用 调用service保存到数据库
                userService.registUser(new User(null, username, password, email));
                //跳到注册成功页面
                req.getRequestDispatcher("/pages/user/regist_success.jsp").forward(req, resp);

            }


        } else {
            //把回显信息 保存到request域中
            req.setAttribute("msg", "验证码错误!");
            req.setAttribute("username", username);
            req.setAttribute("email", email);

            System.out.println("验证码" + code + "错误");
            //跳回注册页面
            req.getRequestDispatcher("/pages/user/regist.jsp").forward(req, resp);
        }
    }
}