package cn.itcast.core.service.staticpage;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName StaticPageServiceImpl
 * @Description 生成商品静态详情页
 * @Author chenyingxin
 * @Date 15:19 2019/3/28
 * @Version 2.1
 */
public class StaticPageServiceImpl implements StaticPageService ,ServletContextAware{

    //用于获取模板
    private Configuration configuration;

    public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
        this.configuration = freeMarkerConfigurer.getConfiguration();
    }

    //用于获取项目发布地址
    private ServletContext servletContext;


    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Resource
    private ItemCatDao itemCatDao;

    @Resource
    private GoodsDao goodsDao;

    @Resource
    private ItemDao itemDao;

    @Resource
    private GoodsDescDao goodsDescDao;

    /**
     * @param id 商品id
     * @return void
     * @Author chenyingxin
     * @Descristion 生成商品静态详情页
     * @Date 15:17 2019/3/28
     */
    @Override
    public void getHtml(Long id) {

        try {
            //获取该位置下的模板
            Template template = configuration.getTemplate("item.ftl");
            //准备数据
            Map<String,Object> dataModel = getDataModel(id);
            //模板+数据=输出
            String path = "/"+id+".html";
            String realPath = servletContext.getRealPath(path);
            File file = new File(realPath);
            OutputStream outputStream = new FileOutputStream(file);
            template.process(dataModel,new OutputStreamWriter(outputStream,"UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String,Object> getDataModel(Long id) {
        Map<String,Object> map = new HashMap<>();

        //封装商品数据
        Goods goods = goodsDao.selectByPrimaryKey(id);
        map.put("goods",goods);
        //封装分类数据
        ItemCat itemCat1 = itemCatDao.selectByPrimaryKey(goods.getCategory1Id());
        ItemCat itemCat2 = itemCatDao.selectByPrimaryKey(goods.getCategory2Id());
        ItemCat itemCat3 = itemCatDao.selectByPrimaryKey(goods.getCategory3Id());

        map.put("itemCat1",itemCat1);
        map.put("itemCat2",itemCat2);
        map.put("itemCat3",itemCat3);
        //封装库存数据
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(id);
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        map.put("itemList",itemList);
        //封装描述数据
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        map.put("goodsDesc",goodsDesc);

        return map;
    }


}
