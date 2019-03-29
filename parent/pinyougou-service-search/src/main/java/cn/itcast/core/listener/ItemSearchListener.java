package cn.itcast.core.listener;

import cn.itcast.core.service.search.ItemSearchService;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * @ClassName ItemSearchListener
 * @Description 获取消息-消费消息
 * @Author chenyingxin
 * @Date 10:44 2019/3/29
 * @Version 2.1
 */
public class ItemSearchListener implements MessageListener {

    @Resource
    private ItemSearchService itemSearchService;
    /**
     * @Author chenyingxin
     * @Descristion 获取消息-消费消息
     * @Date 10:45 2019/3/29
     * @param message
     * @return void
     */
    @Override
    public void onMessage(Message message) {
        try {
            //取出消息体
            ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
            String id = activeMQTextMessage.getText();
            System.out.println("消费者service-search收到的商品id："+id);
            //消费消息
            itemSearchService.addItemToSolr(Long.valueOf(id));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
