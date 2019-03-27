package cn.itcast.core.service.search;

import java.util.Map;

public interface ItemSearchService {

    //商品检索并分页
    Map<String,Object> search(Map<String,String> searchMap);
}
