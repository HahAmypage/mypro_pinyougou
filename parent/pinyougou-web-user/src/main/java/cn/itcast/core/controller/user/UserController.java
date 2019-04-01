package cn.itcast.core.controller.user;

import cn.core.itcast.utils.checkphone.PhoneFormatCheckUtils;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.user.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
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

    /**
     * @Author chenyingxin
     * @Descristion 发送验证码
     * @Date 15:40 2019/3/31
     * @param phone
     * @return cn.itcast.core.pojo.entity.Result
     */
    @RequestMapping("/sendCode.do")
    public Result sendCode(String phone){
        try{
            boolean chinaPhoneLegal = PhoneFormatCheckUtils.isChinaPhoneLegal(phone);
            if (!chinaPhoneLegal){
                return new Result(false,"手机号不合法");
            }
            userService.sendCode(phone);
            return new Result(true,"发送成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"发送失败");
        }
    }

    /**
     * @Author chenyingxin
     * @Descristion 注册
     * @Date 15:42 2019/3/31
     * @param smscode
     * @param user
     * @return cn.itcast.core.pojo.entity.Result
     */
    @RequestMapping("/add.do")
    public Result add(String smscode, @RequestBody User user){
        try {
            userService.add(smscode,user);
            return new Result(true,"注册成功");
        }catch (RuntimeException e){
            return new Result(false,e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"注册失败");
        }
    }
}
