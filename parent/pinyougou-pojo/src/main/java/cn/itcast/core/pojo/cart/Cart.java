package cn.itcast.core.pojo.cart;

import cn.itcast.core.pojo.order.OrderItem;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName Cart
 * @Description 购物车
 * @Author chenyingxin
 * @Date 16:40 2019/4/2
 * @Version 2.1
 */
public class Cart implements Serializable{
    private String sellerId;
    private String shopName;
    private List<OrderItem> orderItemList;

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cart)) return false;
        Cart cart = (Cart) o;
        return Objects.equals(sellerId, cart.sellerId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(sellerId);
    }
}
