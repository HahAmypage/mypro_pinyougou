package cn.itcast.core.service.user;
import cn.itcast.core.pojo.user.User; /**
 * @Author chenyingxin
 * @Descristion 用户服务
 * @Date 20:54 2019/3/30
 * @return
 */
public interface UserService {

    /**
     * @Author chenyingxin
     * @Descristion 发送短信验证码
     * @Date 20:57 2019/3/30
     * @param phone
     * @return void
     */
    void sendCode(String phone);

    /**
     * @Author chenyingxin
     * @Descristion 注册
     * @Date 15:43 2019/3/31
     * @param smscode
     * @param user
     * @return void
     */
    void add(String smscode, User user);
}
