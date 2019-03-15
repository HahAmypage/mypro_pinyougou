package cn.itcast.core.service.brand;

import cn.itcast.core.pojo.good.Brand;

import java.util.List;

public interface BrandService {
    /**
     * 查询所有品牌
     */
    List<Brand> findAll();
}
