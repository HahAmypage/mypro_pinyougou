package cn.itcast.core.service.goods;

import cn.itcast.core.vo.GoodsVo;

public interface GoodsService {

    /**
     * 保存商品
     * @param goodsVo
     */
    void add(GoodsVo goodsVo);
}
