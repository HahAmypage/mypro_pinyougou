package cn.itcast.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @ClassName SmsApplication
 * @Description 发送短信验证码
 * @Author chenyingxin
 * @Date 20:33 2019/3/30
 * @Version 2.1
 */
@SpringBootApplication
public class SmsApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SmsApplication.class);
        application.run(args);
    }
}
