package cn.itcast.core.service.seller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.seller.Seller;


public interface SellerService {

    /**
     * 注册用户
     * @param seller
     */
    void add(Seller seller);

    /**
     * 查询商家
     * @param page
     * @param rows
     * @param seller
     * @return
     */
    PageResult search(Integer page, Integer rows, Seller seller);

    /**
     * 根据id查询商家
     * @param id
     * @return
     */
    Seller findOne(String id);

    /**
     * 更新商家审核状态
     * @param sellerid
     * @param status
     */
    void updateStatus(String sellerid, String status);
}
