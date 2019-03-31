package cn.itcast.core.listener;

import cn.itcast.core.service.search.ItemSearchService;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * @ClassName ItemDeleteListener
 * @Description 监听商品下架
 * @Author chenyingxin
 * @Date 16:39 2019/3/30
 * @Version 2.1
 */
public class ItemDeleteListener implements MessageListener {

    @Resource
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        try {
            //取出消息体
            ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
            String id = activeMQTextMessage.getText();
            System.out.println("删除的id："+id);
            itemSearchService.deleteItemToSolr(Long.valueOf(id));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
