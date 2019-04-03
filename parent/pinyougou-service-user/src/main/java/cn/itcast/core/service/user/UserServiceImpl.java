package cn.itcast.core.service.user;

import cn.core.itcast.utils.md5.MD5Util;
import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.user.User;
import com.alibaba.dubbo.config.annotation.Service;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * @ClassName UserServiceImpl
 * @Description 发送短信验证码
 * @Author chenyingxin
 * @Date 20:58 2019/3/30
 * @Version 2.1
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private JmsTemplate jmsTemplate;

    @Resource
    private Destination smsDestination;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserDao userDao;

    /**
     * @param phone
     * @return void
     * @Author chenyingxin
     * @Descristion 发送短信验证码
     * @Date 20:57 2019/3/30
     */
    @Override
    public void sendCode(final String phone) {

        // 验证码
        final String code = RandomStringUtils.randomNumeric(6);

        System.out.println(code);

        redisTemplate.boundValueOps(phone).set(code);
        //设置验证码过期时间
        redisTemplate.boundValueOps(phone).expire(5, TimeUnit.MINUTES);

        // 将数据发送到mq中
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("phoneNumbers", phone);
                mapMessage.setString("signName", "莹芯");
//                mapMessage.setString("templateCode", "SMS_140720901");
                mapMessage.setString("templateCode","SMS_162547815");
                mapMessage.setString("templateParam", "{\"code\":\""+code+"\"}");
                return mapMessage;
            }
        });
    }

    @Override
    public void add(String smscode, User user) {
        //根据用户手机号到redis查找验证
        String code = (String) redisTemplate.boundValueOps(user.getPhone()).get();
        if (smscode!=null&&!"".equals(smscode)&&smscode.equals(code)){
            //验证码验证成功
            //注册
            //对用户密码加密再注册（MD5）
            String md5Encode = MD5Util.MD5Encode(user.getPassword(),null);
            user.setPassword(md5Encode);
            user.setCreated(new Date());
            user.setUpdated(new Date());
            userDao.insertSelective(user);
        }else {
            throw new RuntimeException("验证码不正确");
        }
    }
}
