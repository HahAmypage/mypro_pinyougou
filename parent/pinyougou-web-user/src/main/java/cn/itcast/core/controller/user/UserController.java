package cn.itcast.core.controller.user;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.service.user.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName UserController
 * @Description 发送短信
 * @Author chenyingxin
 * @Date 21:39 2019/3/30
 * @Version 2.1
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    @RequestMapping("/sendCode.do")
    public Result sendCode(String phone){
        try{
            userService.sendCode(phone);
            return new Result(true,"发送成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"发送失败");
        }
    }
}
