package cn.itcast.core.service.brand;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;

import java.util.List;
import java.util.Map;

public interface BrandService {
    /**
     * 查询所有品牌
     */
    List<Brand> findAll();

    /**
     * 品牌的分页查询
     */
    PageResult findByPage(Integer pageNo,Integer pageSize);

    /**
     * 条件查询
     */
    PageResult search(Integer pageNo,Integer pageSize,Brand brand);

    /**
     * 新增品牌
     * @param brand
     */
    void add(Brand brand);

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    Brand findOne(Long id);

    /**
     * 更新品牌数据
     * @param brand
     */
    void update(Brand brand);

    /**
     * 删除品牌信息
     * @param id
     */
    void delete(Long[] id);

    /**
     * 新增模板-初始化信息
     * @return
     */
    List<Map<String,String>> selectOptionList();
}
