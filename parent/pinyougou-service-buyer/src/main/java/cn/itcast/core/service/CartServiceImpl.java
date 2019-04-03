package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.cart.Cart;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.cart.CartService;
import com.alibaba.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName CartServiceImpl
 * @Description 购物车
 * @Author chenyingxin
 * @Date 16:53 2019/4/2
 * @Version 2.1
 */
@Service
public class CartServiceImpl implements CartService {

    @Resource
    private SellerDao sellerDao;

    @Resource
    private ItemDao itemDao;

    @Resource
    private OrderItemDao orderItemDao;
    /**
     * @param id
     * @return cn.itcast.core.pojo.item.Item
     * @Author chenyingxin
     * @Descristion 获取商家id
     * @Date 16:51 2019/4/2
     */
    @Override
    public Item findOne(Long id) {
        return itemDao.selectByPrimaryKey(id);
    }

    @Override
    public List<Cart> showCartData(List<Cart> cartList) {
        for (Cart cart:cartList){
            //填充商家
            Seller seller = sellerDao.selectByPrimaryKey(cart.getSellerId());
            cart.setShopName(seller.getNickName());

            //填充其他信息
            List<OrderItem> cartCartList = cart.getOrderItemList();
            for (OrderItem orderItem:cartCartList){
                Item item = itemDao.selectByPrimaryKey(orderItem.getItemId());
                orderItem.setTitle(item.getTitle());
                orderItem.setPrice(item.getPrice());
                orderItem.setPicPath(item.getImage());
                BigDecimal total_fee = new BigDecimal(item.getPrice().doubleValue()*orderItem.getNum());
                orderItem.setTotalFee(total_fee);
            }

        }
        return cartList;
    }
}
