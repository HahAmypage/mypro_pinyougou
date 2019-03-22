package cn.itcast.core.service.itemcat;

import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;
import com.alibaba.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ItemCatServiceImpl implements ItemCatService {

    @Resource
    private ItemCatDao itemCatDao;
    /**
     * 根据parentID 查询
     *
     * @param parentId
     * @return
     */
    @Override
    public List<ItemCat> findByParentId(Long parentId) {
        ItemCatQuery itemCatQuery = new ItemCatQuery();
        itemCatQuery.createCriteria().andParentIdEqualTo(parentId);
        return itemCatDao.selectByExample(itemCatQuery);
    }

    /**
     * 添加分类
     *
     * @param itemCat
     * @return
     */
    @Override
    public void add(ItemCat itemCat) {
        itemCatDao.insertSelective(itemCat);
    }

    /**
     * 根据id查询指定分类
     *
     * @param id
     * @return
     */
    @Override
    public ItemCat findOne(Long id) {
        return itemCatDao.selectByPrimaryKey(id);
    }

    /**
     * 修改分类
     *
     * @param itemCat
     */
    @Override
    public void update(ItemCat itemCat) {
        itemCatDao.updateByPrimaryKeySelective(itemCat);
    }

    /**
     * 删除分类
     *
     * @param ids
     */
    @Override
    public void delete(Long[] ids) {
        if (ids!=null&& ids.length>0){
            for (Long id : ids){
//                itemCatDao.deleteByPrimaryKey(id);
            }
        }
    }
}
