package cn.itcast.core.service.user;
/**
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
}
