package cn.itcast.core.listener;

import cn.itcast.core.service.staticpage.StaticPageService;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * @ClassName PageListener
 * @Description 获取消息-消费消息
 * @Author chenyingxin
 * @Date 11:01 2019/3/29
 * @Version 2.1
 */
public class PageListener implements MessageListener{

    @Resource
    private StaticPageService staticPageService;

    /**
     * @Author chenyingxin
     * @Descristion 获取消息-消费消息
     * @Date 11:04 2019/3/29
     * @param message
     * @return void
     */
    @Override
    public void onMessage(Message message) {
        //获取消息体
        try {
            ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
            String id = activeMQTextMessage.getText();
            System.out.println("消费者service-page获取的商品id："+id);
            staticPageService.getHtml(Long.valueOf(id));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
