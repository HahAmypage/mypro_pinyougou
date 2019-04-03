package cn.itcast.core.controller.cart;

import cn.itcast.core.pojo.cart.Cart;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.cart.CartService;
import cn.itcast.core.service.seller.SellerService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName CartController
 * @Description 购物车
 * @Author chenyingxin
 * @Date 15:29 2019/4/2
 * @Version 2.1
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Reference
    private SellerService sellerService;

    /**
     * @Author chenyingxin
     * @Descristion 添加商品到购物车
     * @Date 15:30 2019/4/2
     * @param
     * @return cn.itcast.core.pojo.entity.Result
     */
    @RequestMapping("/addGoodsToCartList.do")
    //注意这里配置的地址是详情页（跳转前）
    @CrossOrigin(origins = {"http://localhost:19003"})
    public Result addGoodsToCartList(Long itemId, Integer num, HttpServletResponse response, HttpServletRequest request){
        try{
//            //服务端支持CORS
//            response.setHeader("Access-Control-Allow-Origin","http://localhost:19003");
//            //携带cookie
////            response.setHeader("Access-Control-Allow-Credentials","true");
            //加入购物车业务代码
            //1、定义一个空车集合
            List<Cart> cartList = null;
            //2、判断本地是否有车子
            Cookie[] cookies = request.getCookies();
            if (cookies!=null&&cookies.length>0){
                for (Cookie cookie:cookies){
                    if (cookie.getName().equals("SHOPPING_CART")){
                        //3、如果有，取出本地的购物车
                        cartList = JSON.parseArray(cookie.getValue(),Cart.class);
                        break;
                    }else {
                        //4、如果cookie中没有购物车，创建一个购物车
                        cartList = new ArrayList<>();
                    }
                }
            }else {
                //4、如果没有cookie或者cookie为空串，创建一个购物车
                cartList = new ArrayList<>();
            }


            //5、有购物车以后，将商品添加到购物车
            Item item = cartService.findOne(itemId);
            Cart cart = new Cart();
            cart.setSellerId(item.getSellerId()); //封装商家id
            Seller seller = sellerService.findOne(item.getSellerId());
            cart.setShopName(seller.getSellerId()); //封装商店名

            //封装商品项
            List<OrderItem> orderItemList = new ArrayList<>();
            //因为cookie有长度限制，所以只存关键内容（sellerid ，itemid ，num）
            OrderItem orderItem = new OrderItem();
            orderItem.setItemId(Long.valueOf(itemId));
            orderItem.setNum(num);

            orderItemList.add(orderItem);

            cart.setOrderItemList(orderItemList);
            //5.1、先判断商品商家是否已在购物车
            int sellerIndex = cartList.indexOf(cart);

            if (sellerIndex!=-1){
                List<OrderItem> oldCartList = cartList.get(sellerIndex).getOrderItemList();
                //判断是否有相同的商品
                int indexItem = oldCartList.indexOf(orderItem);
                if (indexItem!=-1){
                    //有相同的商品，合并数量
                    OrderItem orderItem1 = oldCartList.get(indexItem);
                    orderItem1.setNum(orderItem1.getNum()+num);
                }else {
                    //无相同商品，把商品添加到商品项列表
                    oldCartList.add(orderItem);
                }
            }else {
                //商家不存在,直接添加
                cartList.add(cart);
            }

            //把购物车存到cookie（假如使用Tomcat8，要改为url编码和解码）
            Cookie cookie = new Cookie("SHOPPING_CART",JSON.toJSONString(cartList));
            cookie.setMaxAge(60*60);
            cookie.setPath("/");
            response.addCookie(cookie);

            return new Result(true,"加入购物车成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"加入购物车失败");
        }
    }


    /**
     * @Author chenyingxin
     * @Descristion 购物车回显
     * @Date 20:35 2019/4/2
     * @param request
     * @return java.util.List<cn.itcast.core.pojo.cart.Cart>
     */
    @RequestMapping("/findCartList.do")
    public List<Cart> findCartList(HttpServletRequest request){
        List<Cart> cartList = null;
        Cookie[] cookies = request.getCookies();
        if (cookies!=null&&cookies.length>0){
            //查询是否有购物车
            for (Cookie cookie : cookies){
                boolean shopping_cart = cookie.getName().equals("SHOPPING_CART");
                if (shopping_cart){
                    //如果有购物车，交给service填充数据
                    cartList = JSON.parseArray(cookie.getValue(), Cart.class);
                    break;

                }
            }
        }
        if (cartList!=null){
            cartList = cartService.showCartData(cartList);
        }
        return cartList;
    }
}
