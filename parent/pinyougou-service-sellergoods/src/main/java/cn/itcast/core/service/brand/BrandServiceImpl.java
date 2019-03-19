package cn.itcast.core.service.brand;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Resource
    private BrandDao brandDao;

    /**
     * 查询所有品牌
     */
    @Override
    public List<Brand> findAll() {
        return brandDao.selectByExample(null);
    }

    /**
     * 品牌的分页查询
     *
     * @param pageNo
     * @param pageSize
     */
    @Override
    public PageResult findByPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(null);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 条件查询
     *
     * @param pageNo
     * @param pageSize
     * @param brand
     */
    @Override
    public PageResult search(Integer pageNo, Integer pageSize, Brand brand) {
        //分页
        PageHelper.startPage(pageNo,pageSize);
        //设置查询条件：封装条件对象
        BrandQuery brandQuery = new BrandQuery();
        //封装具体查询调教对象
        BrandQuery.Criteria criteria = brandQuery.createCriteria();
        //拼接SQL
        if (brand.getName()!=null && !"".equals(brand.getName().trim())){
            criteria.andNameLike("%"+brand.getName().trim()+"%");
        }
        if (brand.getFirstChar()!=null && !"".equals(brand.getFirstChar().trim())){
            criteria.andFirstCharEqualTo(brand.getFirstChar().trim());
        }
        brandQuery.setOrderByClause("id desc");
        //进行查询
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(brandQuery);
        //将结果封装到PageResult
        PageResult pageResult = new PageResult(page.getTotal(),page.getResult());
        return pageResult;
    }

    /**
     * 新增品牌
     * @param brand
     */
    @Override
    public void add(Brand brand) {
        brandDao.insertSelective(brand);
    }

    /**
     * 根据id查询品牌
     *
     * @param id
     * @return
     */
    @Override
    public Brand findOne(Long id) {
        return brandDao.selectByPrimaryKey(id);
    }

    /**
     * 更新品牌数据
     *
     * @param brand
     */
    @Transactional
    @Override
    public void update(Brand brand) {
        brandDao.updateByPrimaryKeySelective(brand);
    }

    /**
     * 删除品牌信息
     *
     * @param ids
     */
    @Override
    public void delete(Long[] ids) {
        if (ids!=null&&ids.length>0){
//            for (Long id :ids){
//                brandDao.deleteByPrimaryKey(id);
//            }
            brandDao.deleteBatch(ids);
        }
    }

    /**
     * 新增模板-初始化信息
     *
     * @return
     */
    @Override
    public List<Map<String, String>> selectOptionList() {
        return brandDao.selectOptionList();
    }
}
