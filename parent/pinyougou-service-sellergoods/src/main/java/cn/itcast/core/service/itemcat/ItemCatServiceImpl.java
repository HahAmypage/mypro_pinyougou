package cn.itcast.core.service.itemcat;

import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
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
    @Transactional
    @Override
    public void update(ItemCat itemCat) {
        itemCatDao.updateByPrimaryKeySelective(itemCat);
    }

    /**
     * 删除分类
     *
     * @param ids
     */
    @Transactional
    @Override
    public void delete(Long[] ids) {
        if (ids!=null&& ids.length>0){
            //查询所有节点
            List<ItemCat> itemCatList = itemCatDao.selectByExample(null);
            //方式一：递归
            for (ItemCat itemCat:itemCatList){
                //遍历要删除的id
                for (Long id :ids){
                    if (id.equals(itemCat.getId())){
                        //查找该分类下是否有子分类
                        ItemCatQuery itemCatQuery = new ItemCatQuery();
                        itemCatQuery.createCriteria().andParentIdEqualTo(itemCat.getId());
                        List<ItemCat> itemCats = itemCatDao.selectByExample(itemCatQuery);
                        if (itemCats!=null&&itemCats.size()>0){
                            //如果有子分类,创建一个数组存放子分类的id
                            Long[] subCatId = new Long[itemCats.size()];
                            for (int i = 0 ;i<itemCats.size();i++){
                                subCatId[i]=itemCats.get(i).getId();
                            }
                            delete(subCatId);
                        }
//                        itemCatDao.deleteByPrimaryKey(id);
                    }
                    //TODO:待测试
                    itemCatDao.BatchDeleteByPrimaryKey(ids);
                }
            }

            //方式二：
//            for (Long id :ids){
//                itemCatDao.deleteByPrimaryKey(id);
//                //查询该分类下是否有子分类
//                ItemCatQuery query = new ItemCatQuery();
//                query.createCriteria().andParentIdEqualTo(id);
//                List<ItemCat> itemCats = itemCatDao.selectByExample(query);
//                if(itemCats!=null&&itemCats.size()>0){
//                    //如果有子分类(二级)
//                    for (ItemCat itemCat:itemCats){
//                        itemCatDao.deleteByPrimaryKey(itemCat.getId());
//                        ItemCatQuery itemCatQuery = new ItemCatQuery();
//                        itemCatQuery.createCriteria().andParentIdEqualTo(itemCat.getId());
//                        List<ItemCat> itemCats2 = itemCatDao.selectByExample(itemCatQuery);
//                        if (itemCats2!=null&&itemCats2.size()>0){
//                            //如果有子分类(三级)
//                            for (ItemCat itemCat1:itemCats2){
//                                itemCatDao.deleteByPrimaryKey(itemCat1.getId());
//                            }
//                        }
//                    }
//                }
//            }
        }
    }

    /**
     * 查询所有分类
     *
     * @return
     */
    @Override
    public List<ItemCat> findAll() {
        return itemCatDao.selectByExample(null);
    }
}
