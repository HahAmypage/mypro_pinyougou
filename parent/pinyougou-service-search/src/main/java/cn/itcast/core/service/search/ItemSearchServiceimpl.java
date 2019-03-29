package cn.itcast.core.service.search;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.pojo.order.OrderQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.logging.Filter;

@Service
public class ItemSearchServiceimpl implements ItemSearchService{

    @Resource
    private SolrTemplate solrTemplate;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private ItemDao itemDao;


    /**
     * @Author chenyingxin
     * @Descristion 商品上架-保存到索引库
     * @Date 10:43 2019/3/29
     * @param id
     * @return void
     */
    @Override
    public void addItemToSolr(Long id) {
        ItemQuery itemQuery = new ItemQuery();
        //根据id查询状态正常，并且默认（只显示多个规格中勾选默认的一项），以及库存大于0的商品
        itemQuery.createCriteria().andGoodsIdEqualTo(id).andStatusEqualTo("1").andIsDefaultEqualTo("1").andNumGreaterThan(0);
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        if (itemList!=null&&itemList.size()>0){
            for (Item item:itemList){
                //处理规格：{"机身内存":"16G","网络":"联通3G"}
                Map<String,String> map = JSON.parseObject(item.getSpec(), Map.class);
                item.setSpecMap(map);
            }
            //添加到索引库
            solrTemplate.saveBeans(itemList);
            solrTemplate.commit();
        }
    }


    /**
     * 前台系统-商品检索
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {

        //使用一个Map封装所有结果
        Map<String,Object> resultMap = new HashMap<>();
        //根据关键字检索并分页
        Map<String, Object> searchForPage = searchHighlightForPage(searchMap);
        resultMap.putAll(searchForPage);

        //查询商品分类信息
        List<String> categoryList = searchForGroupPage(searchMap);
        if (categoryList!=null&&categoryList.size()>0){
            resultMap.put("categoryList",categoryList);
            //加载第一个分类的品牌和规格
            Map<String, Object> brandAndSpecMap = searchBrandAndSpeForFirstTypeTemplate(categoryList.get(0));
            resultMap.putAll(brandAndSpecMap);
        }
        return resultMap;
    }

    //加载分类下的品牌以及规格
    private Map<String,Object> searchBrandAndSpeForFirstTypeTemplate(String category){
        Map<String,Object> map = new HashMap<>();
        //默认只查第一个分类
        Object categoryId = redisTemplate.boundHashOps("itemCat").get(category);
        //根据模板id去redis查询品牌
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(categoryId);
        //根据模板id去redis查询规格（根据规格查规格列表）
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(categoryId);

        map.put("brandList",brandList);
        map.put("specList",specList);

        return map;
    }

    //查询商品分类信息
    private List<String> searchForGroupPage(Map<String,String> searchMap){
        // 1、设置关键字条件
        Criteria criteria = new Criteria("item_keywords");
        //这里拿条件要根据js传来的searchMap中的key
        String keywords = searchMap.get("keywords");
        if (keywords!=null&&!"".equals(keywords)){
            criteria.is(keywords);
        }
        //设置分组条件
        SimpleQuery simpleQuery = new SimpleQuery(criteria);
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        simpleQuery.setGroupOptions(groupOptions);
        //根据分组条件查询
        GroupPage<Item> groupPage = solrTemplate.queryForGroupPage(simpleQuery, Item.class);
        //将结果封装到list
        List<String> categoryList = new ArrayList<>();
        GroupResult<Item> groupResult = groupPage.getGroupResult("item_category");
        Page<GroupEntry<Item>> groupEntries = groupResult.getGroupEntries();
        for (GroupEntry<Item> groupEntry:groupEntries){
            String groupValue = groupEntry.getGroupValue();
            categoryList.add(groupValue);
        }
        return categoryList;
    }

    //查询检索结果（关键字高亮）
    private Map<String,Object> searchHighlightForPage(Map<String,String> map){
        //设置查询条件
        String keywords = map.get("keywords");
        //处理关键字的空格
        keywords = keywords.replaceAll(" ", "");
        //替换为无空格的关键字
        map.put("keywords",keywords);
        Criteria criteria = new Criteria("item_keywords");
        if (keywords!=null&&!"".equals(keywords)){
            criteria.is(keywords);
        }
        SimpleHighlightQuery highlightQuery = new SimpleHighlightQuery(criteria);
        //设置分页条件
        Integer pageNo = Integer.valueOf(map.get("pageNo"));
        Integer pageSize = Integer.valueOf(map.get("pageSize"));
        Integer start = (pageNo-1)*pageSize;
        highlightQuery.setOffset(start);
        highlightQuery.setRows(pageSize);

        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");
        highlightOptions.setSimplePrefix("<font color='red'>");
        highlightOptions.setSimplePostfix("</font>");
        highlightQuery.setHighlightOptions(highlightOptions);

        //条件过滤
        // 1)根据类别过滤
        String category = map.get("category");
        if(category!=null&&!"".equals(category)){
            Criteria criteriaCategory = new Criteria("item_category");
            criteriaCategory.is(category);
            //注意criteria要加到Query中
            SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(criteriaCategory);
            highlightQuery.addFilterQuery(simpleFilterQuery);
        }

        // 2)根据品牌过滤
        String brand = map.get("brand");
        if (brand!=null&&!"".equals(brand)){
            Criteria criteria1Brand = new Criteria("item_brand");
            criteria1Brand.is(brand);
            SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(criteria1Brand);
            highlightQuery.addFilterQuery(simpleFilterQuery);
        }

        // 3)根据规格过滤
        String specList = map.get("spec");
        if (specList!=null&&!"".equals(specList)){
            //{"机身内存":"16G","网络":"联通3G"}
            Map<String,String> specMap = JSON.parseObject(specList, Map.class);
            Set<Map.Entry<String, String>> entries = specMap.entrySet();
            for (Map.Entry<String,String> entry:entries){
                Criteria criteriaSpec = new Criteria("item_spec_"+entry.getKey());
                criteriaSpec.is(entry.getValue());
                SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(criteriaSpec);
                highlightQuery.addFilterQuery(simpleFilterQuery);
            }
        }

        //关键字搜索之排序
        String sortField = map.get("sortField");
        String sortMode = map.get("sort");
        if (sortMode!=null&&!"".equals(sortMode)){
            if ("ASC".equals(sortMode)){
                Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
                highlightQuery.addSort(sort);
            }else {
                Sort sort = new Sort(Sort.Direction.DESC,"item_"+sortField);
                highlightQuery.addSort(sort);
            }
        }


        //查询
        HighlightPage<Item> highlightPage = solrTemplate.queryForHighlightPage(highlightQuery, Item.class);
        //处理高亮结果集
        List<HighlightEntry<Item>> highlighted = highlightPage.getHighlighted();
        if (highlighted!=null&&highlighted.size()>0){
            for (HighlightEntry<Item> highlightEntry : highlighted){
                //普通结果
                Item item = highlightEntry.getEntity();
                //获取高亮结果
                List<HighlightEntry.Highlight> entryHighlights = highlightEntry.getHighlights();
                if (entryHighlights!=null&&entryHighlights.size()>0){
                    String title = entryHighlights.get(0).getSnipplets().get(0);
                    item.setTitle(title);
                }
            }
        }

        //封装数据
        Map<String,Object> highlightMap = new HashMap<>();

        highlightMap.put("totalPages",highlightPage.getTotalPages());
        highlightMap.put("total",highlightPage.getTotalElements());
        highlightMap.put("rows",highlightPage.getContent());

        return highlightMap;
    }




    //普通搜索（没有高亮）
    private Map<String,Object> searchForPage(Map<String, String> searchMap) {
        //设置查询条件
        Criteria criteria = new Criteria("item_keywords");
        String keywords = searchMap.get("keywords");
        //注意is相当于SQL里面的%%
        if (keywords!=null&& !"".equals(keywords)){
            criteria.is(keywords);
        }
        //设置分页条件
        SimpleQuery simpleQuery = new SimpleQuery(criteria);

        Integer pageNo = Integer.valueOf(searchMap.get("pageNo"));
        Integer pageSize = Integer.valueOf(searchMap.get("pageSize"));
        Integer start = (pageNo-1)*pageSize;
        simpleQuery.setOffset(start);
        simpleQuery.setRows(pageSize);
        //查询
        ScoredPage<Item> itemScoredPage = solrTemplate.queryForPage(simpleQuery, Item.class);
        //用于封装查询结果

        Map<String,Object> map = new HashMap<>();

        map.put("totalPages",itemScoredPage.getTotalPages());
        map.put("total",itemScoredPage.getTotalElements());
        //item list要取出来再封装
        map.put("rows",itemScoredPage.getContent());

        return map;
    }
}
