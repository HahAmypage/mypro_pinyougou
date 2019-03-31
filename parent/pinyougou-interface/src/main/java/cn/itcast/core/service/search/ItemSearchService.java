package cn.itcast.core.service.search;

import java.util.Map;

public interface ItemSearchService {

    /**
     * @Author chenyingxin
     * @Descristion 商品检索并分页
     * @Date 10:40 2019/3/29
     * @param searchMap
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    Map<String,Object> search(Map<String,String> searchMap);

    /**
     * @Author chenyingxin
     * @Descristion 商品上架-保存到索引库
     * @Date 10:39 2019/3/29
     * @param id
     * @return void
     */
    void addItemToSolr(Long id);

    /**
     * @Author chenyingxin
     * @Descristion 商品下架
     * @Date 16:37 2019/3/30
     * @param id
     * @return void
     */
    void deleteItemToSolr(Long id);
}
