package cn.itcast.core.task;

import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @ClassName RedisTask
 * @Description 定时同步缓存
 * @Author chenyingxin
 * @Date 21:59 2019/3/27
 * @Version 2.1
 */
@Component
public class RedisTask {

    @Resource
    private ItemCatDao itemCatDao;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private TypeTemplateDao typeTemplateDao;

    @Resource
    private SpecificationOptionDao specificationOptionDao;


    /**
     * @Author chenyingxin
     * @Descristion 将分类信息放入缓存
     * @Date 22:20 2019/3/27
     * @param
     * @return void
     */
    @Scheduled(cron = "20 38 23 27 03 *")
    public void autoItemCatToRedis(){
        List<ItemCat> itemCats = itemCatDao.selectByExample(null);
        if(itemCats!=null&&itemCats.size()>0){
            for (ItemCat itemCat:itemCats){
                redisTemplate.boundHashOps("itemCat").put(itemCat.getName(),itemCat.getTypeId());
            }
        }
    }

    @Scheduled(cron = "20 38 23 27 03 *")
    public void autoTypeTemplateToRedis(){
        List<TypeTemplate> typeTemplates = typeTemplateDao.selectByExample(null);
        if (typeTemplates!=null&&typeTemplates.size()>0){
            for (TypeTemplate typeTemplate:typeTemplates){
                String brandIds = typeTemplate.getBrandIds();
                List<Map> brandList = JSON.parseArray(brandIds, Map.class);
                if (brandList!=null&&brandList.size()>0){
                    redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(),brandList);
                }
                List<Map> specList = findBySpecList(typeTemplate.getId());
                if (specList!=null&&specList.size()>0){
                    redisTemplate.boundHashOps("specList").put(typeTemplate.getId(),specList);
                }
            }
        }
    }

    private List<Map> findBySpecList(Long id) {
        //根据模板id获取规格
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        String specIds = typeTemplate.getSpecIds();
        List<Map> specList = JSON.parseArray(specIds, Map.class);
        //根据规格获取规格选项
        if (specList!=null&&specList.size()>0){
            for (Map map:specList){
                Long specId = Long.parseLong(map.get("id").toString());
                //通过规格id获取规格选项
                SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
                specificationOptionQuery.createCriteria().andSpecIdEqualTo(specId);
                List<SpecificationOption> specificationOptions = specificationOptionDao.selectByExample(specificationOptionQuery);
                map.put("options",specificationOptions);
            }
        }
        return specList;
    }

}
