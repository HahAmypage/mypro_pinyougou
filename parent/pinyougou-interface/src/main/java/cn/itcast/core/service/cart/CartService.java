package cn.itcast.core.service.cart;

import cn.itcast.core.pojo.cart.Cart;
import cn.itcast.core.pojo.item.Item;

import java.util.List;

/**
 * @ClassName CartService
 * @Description 加入购物车
 * @Author chenyingxin
 * @Date 16:49 2019/4/2
 * @Version 2.1
 */
public interface CartService {

    /**
     * @Author chenyingxin
     * @Descristion 获取商家id
     * @Date 16:51 2019/4/2
     * @param id
     * @return cn.itcast.core.pojo.item.Item
     */
    Item findOne(Long id);

    /**
     * @Author chenyingxin
     * @Descristion 回显购物车
     * @Date 20:54 2019/4/2
     * @param cartList
     * @return java.util.List<cn.itcast.core.pojo.cart.Cart>
     */
    List<Cart> showCartData(List<Cart> cartList);
}
