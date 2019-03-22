package cn.itcast.core.service.goods;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.vo.GoodsVo;
import com.alibaba.dubbo.config.annotation.Service;

import javax.annotation.Resource;

@Service
public class GoodsServiceImpl implements GoodsService {
    @Resource
    private GoodsDao goodsDao;

    @Resource
    private GoodsDescDao goodsDescDao;

    @Resource
    private ItemDao itemDao;
    /**
     * 保存商品
     *
     * @param goodsVo
     */
    @Override
    public void add(GoodsVo goodsVo) {
        if (goodsVo!=null){
            Goods goods = goodsVo.getGoods();
            goods.setAuditStatus("0");
            goodsDao.insertSelective(goods);
            GoodsDesc goodsDesc = goodsVo.getGoodsDesc();
            goodsDesc.setGoodsId(goods.getId());
            goodsDescDao.insertSelective(goodsDesc);
        }

    }
}
