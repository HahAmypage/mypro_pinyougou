package cn.itcast.core.service.goods;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.vo.GoodsVo;

public interface GoodsService {

    /**
     * 保存商品
     * @param goodsVo
     */
    void add(GoodsVo goodsVo);

    /**
     * 商家系统--商品分页查询
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    PageResult searchForShop(Integer page, Integer rows, Goods goods);

    /**
     * 修改商品-回显
     * @param id
     * @return
     */
    GoodsVo findOne(Long id);

    /**
     * 更新商品信息
     * @param goodsVo
     * @return
     */
    void update(GoodsVo goodsVo);

    /**
     * 更新审核状态
     * @param ids
     * @param status
     */
    void updateStatus(Long[] ids, String status);

    /**
     * 运营商系统-查询商品
     * @return
     */
    PageResult searchForManager(Integer page,Integer row,Goods goods);

    /**
     * 删除
     * @param ids
     */
    void delete(Long[] ids);

}
