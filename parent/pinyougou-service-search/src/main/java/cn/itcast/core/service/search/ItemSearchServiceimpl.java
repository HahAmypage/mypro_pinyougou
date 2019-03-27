package cn.itcast.core.service.search;

import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.item.Item;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceimpl implements ItemSearchService{

    @Resource
    private SolrTemplate solrTemplate;

    @Resource
    private RedisTemplate redisTemplate;

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
