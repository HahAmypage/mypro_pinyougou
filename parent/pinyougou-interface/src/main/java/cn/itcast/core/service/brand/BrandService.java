package cn.itcast.core.service.brand;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;

import java.util.List;

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
}
