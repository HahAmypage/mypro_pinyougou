package cn.itcast.core.service.goods;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.vo.GoodsVo;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.opensaml.xml.signature.G;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class GoodsServiceImpl implements GoodsService {
    @Resource
    private GoodsDao goodsDao;

    @Resource
    private GoodsDescDao goodsDescDao;

    @Resource
    private ItemDao itemDao;

    @Resource
    private ItemCatDao itemCatDao;

    @Resource
    private BrandDao brandDao;

    @Resource
    public SellerDao sellerDao;

    @Resource
    private SolrTemplate solrTemplate;

    /**
     * 保存商品
     *
     * @param goodsVo
     */
    @Override
    @Transactional
    public void add(GoodsVo goodsVo) {
        if (goodsVo!=null){
            Goods goods = goodsVo.getGoods();
            goods.setAuditStatus("0");
            goodsDao.insertSelective(goods);
            GoodsDesc goodsDesc = goodsVo.getGoodsDesc();
            goodsDesc.setGoodsId(goods.getId());
            goodsDescDao.insertSelective(goodsDesc);

            //有规格的商品库存可能有多条，没有规格的商品库存只有一条
            if ("1".equals(goods.getIsEnableSpec())){
                //启用规格
                List<Item> itemList = goodsVo.getItemList();
                if (itemList!=null&&itemList.size()>0){
                    for (Item item:itemList){
                        String title = goods.getGoodsName()+" "+goods.getCaption()+" ";
                        //前端传来的数据{"机身内存":"16G","网络":"联通3G"}
                        String spec = item.getSpec();
                        Map<String,String> map = JSON.parseObject(spec,Map.class);
                        Set<Map.Entry<String,String>> entries = map.entrySet();
                        for (Map.Entry<String,String> entry:entries){
                            title+= " "+entry.getValue();
                        }
                        item.setTitle(title);
                        setAttributeForItem(goods,goodsDesc,item);
                        itemDao.insertSelective(item);
                    }
                }
            }else {
                //没启用规格
                Item item = new Item();
                String title = goods.getGoodsName()+" "+goods.getCaption()+" ";
                item.setTitle(title);
                item.setPrice(goods.getPrice());
                item.setNum(100);
                item.setSpec("{}");
                setAttributeForItem(goods, goodsDesc, item);
                itemDao.insertSelective(item);
            }
        }

    }

    /**
     * 商品分页查询
     *
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    @Override
    public PageResult searchForShop(Integer page, Integer rows, Goods goods) {
        //设置分页条件
        PageHelper.startPage(page,rows);
        //设置查询条件
        GoodsQuery goodsQuery = new GoodsQuery();
        GoodsQuery.Criteria criteria = goodsQuery.createCriteria();
        if (goods.getAuditStatus()!=null&&!"".equals(goods.getAuditStatus().trim())){
            criteria.andAuditStatusEqualTo(goods.getAuditStatus().trim());
        }
        if (goods.getGoodsName()!=null&&!"".equals(goods.getGoodsName().trim())){
            criteria.andGoodsNameLike("%"+goods.getGoodsName().trim()+"%");
        }
        if(goods.getSellerId()!=null&&!"".equals(goods.getSellerId().trim())){
            criteria.andSellerIdEqualTo(goods.getSellerId());
        }
        //查询
        Page<Goods> goodsPage = (Page<Goods>) goodsDao.selectByExample(goodsQuery);
        //封装并返回查询结果
        return new PageResult(goodsPage.getTotal(),goodsPage.getResult());
    }

    /**
     * 修改商品-回显
     * @param id
     * @return
     */
    @Override
    public GoodsVo findOne(Long id) {
        //查询商品
        Goods goods = goodsDao.selectByPrimaryKey(id);
        //查询商品详细信息
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        //根据good_id查询
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(id);
        List<Item> items = itemDao.selectByExample(itemQuery);
        //封装数据
        GoodsVo goodsVo = new GoodsVo();
        goodsVo.setGoods(goods);
        goodsVo.setGoodsDesc(goodsDesc);
        goodsVo.setItemList(items);
        return goodsVo;
    }

    /**
     * 更新商品信息
     *
     * @param goodsVo
     * @return
     */
    @Transactional
    @Override
    public void update(GoodsVo goodsVo) {
        Goods goods = goodsVo.getGoods();
        goodsDao.updateByPrimaryKeySelective(goods);
        GoodsDesc goodsDesc = goodsVo.getGoodsDesc();
        goodsDescDao.updateByPrimaryKeySelective(goodsDesc);
        //商品规格信息先删除，在插入
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(goods.getId());
        itemDao.deleteByExample(itemQuery);

        if ("1".equals(goods.getIsEnableSpec())){
            //启用规格
            List<Item> itemList = goodsVo.getItemList();
            if (itemList !=null&&itemList.size()>0){
                for (Item item:itemList){
                    String title = goods.getGoodsName()+" "+goods.getCaption()+" ";
                    String spectStr = item.getSpec();
                    Map<String,String> map = JSON.parseObject(spectStr,Map.class);
                    Set<Map.Entry<String, String>> entries = map.entrySet();
                    for (Map.Entry<String,String> entity:entries){
                        title+=entity.getValue();
                    }
                    item.setTitle(title);
                    setAttributeForItem(goods,goodsDesc,item);
                    itemDao.insertSelective(item);
                }
            }
        }else {
            //没启用规格
            Item item = new Item();
            String title = goods.getGoodsName()+" "+goods.getCaption();
            item.setTitle(title);
            item.setPrice(goods.getPrice());
            item.setNum(100);
            item.setSpec("{}");
            setAttributeForItem(goods,goodsDesc,item);
            itemDao.insertSelective(item);
        }
    }

    /**
     * 更新审核状态
     *
     * @param ids
     * @param status
     */
    @Transactional
    @Override
    public void updateStatus(Long[] ids, String status) {
        if (ids!=null&&ids.length>0){
            Goods goods = new Goods();
            goods.setAuditStatus(status);
            for (Long id:ids){
                goods.setId(id);
                goodsDao.updateByPrimaryKeySelective(goods);
                if ("1".equals(status)){
                    //2、添加到索引库
                    dataImportSolr();

                    //TODO：3、生成静态页面
                }
            }
        }
    }

    //添加商品信息到solr（注意这里只是把所有状态正常的商品添加进去）
    private void dataImportSolr() {
        //根据商品的id查询所有审核成功的item
        ItemQuery itemQuery = new ItemQuery();
        //这里使用正常状态为1的条件是为了后面搜索有更多数据，
        // 正常情况应该是勾选了哪个商品就只添加哪个商品到索引
        itemQuery.createCriteria().andStatusEqualTo("1");
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        if(itemList!=null&&itemList.size()>0){
            for (Item item:itemList){
                //处理一下规格，item中的spec{"机身内存":"16G","网络":"联通3G"}
                Map<String,String> map = JSON.parseObject(item.getSpec(), Map.class);
                //把处理好的规格信息，封装到item的specMap（刚刚新增的字段）
                item.setSpecMap(map);
            }
            //添加到索引库
            solrTemplate.saveBeans(itemList);
            //提交
            solrTemplate.commit();
        }
    }

    /**
     * 运营商系统-查询商品
     *
     * @param page
     * @param row
     * @param goods
     * @return
     */
    @Override
    public PageResult searchForManager(Integer page, Integer row, Goods goods) {
        //设置分页条件
        PageHelper.startPage(page,row);
        //设置查询条件
        GoodsQuery goodsQuery = new GoodsQuery();
        GoodsQuery.Criteria criteria = goodsQuery.createCriteria();
        if (goods.getGoodsName()!=null&&"".equals(goods.getGoodsName().trim())){
            criteria.andGoodsNameLike("%"+goods.getGoodsName().trim()+"%");
        }
        if (goods.getAuditStatus()!=null&&!"".equals(goods.getAuditStatus().trim())){
            criteria.andAuditStatusEqualTo(goods.getAuditStatus());
        }
        //注意是否删除字段的这个条件的封装方式

        //设置排序
        criteria.andIsDeleteIsNull();
        goodsQuery.setOrderByClause("id desc");
        //查询
        Page<Goods> goodsPage= (Page<Goods>) goodsDao.selectByExample(goodsQuery);
        //封装数据并返回
        return new PageResult(goodsPage.getTotal(),goodsPage.getResult());
    }

    /**
     * 删除
     *
     * @param ids
     */
    @Override
    public void delete(Long[] ids) {
        if (ids!=null&&ids.length>0){
            Goods goods = new Goods();
            goods.setIsDelete("1");
            for (Long id : ids){
                goods.setId(id);
                goodsDao.updateByPrimaryKeySelective(goods);
            }
        }
    }


    /**
     * 封装库存信息
     * @param goods
     * @param goodsDesc
     * @param item
     */
    private void setAttributeForItem(Goods goods, GoodsDesc goodsDesc, Item item) {
        String itemImages = goodsDesc.getItemImages();
        List<Map> images = JSON.parseArray(itemImages,Map.class);
        if (images!=null&&images.size()>0){
            String image = images.get(0).get("url").toString();
            item.setImage(image);
        }
        item.setCategoryid(goods.getCategory3Id());
        item.setStatus("1");
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        item.setGoodsId(goods.getId());
        item.setSellerId(goods.getSellerId());
        item.setCategory(itemCatDao.selectByPrimaryKey(goods.getCategory3Id()).getName());
        item.setBrand(brandDao.selectByPrimaryKey(goods.getBrandId()).getName());
        item.setSeller(sellerDao.selectByPrimaryKey(goods.getSellerId()).getName());
    }

}
