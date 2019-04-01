package cn.itcast.core.controller.user;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName LoginController
 * @Description 登录后显示当前登录人
 * @Author chenyingxin
 * @Date 10:11 2019/4/1
 * @Version 2.1
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/name.do")
    public Map<String,String> showName(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String,String> map = new HashMap<>();
        map.put("loginName",name);
        return map;
    }
}
