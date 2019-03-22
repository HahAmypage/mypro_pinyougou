package cn.itcast.core.service.itemcat;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.item.ItemCat;

import java.util.List;

public interface ItemCatService {

    /**
     * 根据parentID 查询
     * @param parentId
     * @return
     */
    List<ItemCat> findByParentId(Long parentId);

    /**
     * 添加分类
     * @param itemCat
     * @return
     */
    void add(ItemCat itemCat);

    /**
     * 根据id查询指定分类
     * @param id
     * @return
     */
    ItemCat findOne(Long id);

    /**
     * 修改分类
     * @param itemCat
     */
    void update(ItemCat itemCat);

    /**
     * 删除分类
     * @param ids
     */
    void delete(Long[] ids);
}
