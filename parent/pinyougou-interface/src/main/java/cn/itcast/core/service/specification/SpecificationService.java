package cn.itcast.core.service.specification;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.vo.SpecificationVo;

public interface SpecificationService {
    /**
     * 显示规格的分页数据
     * @param page
     * @param rows
     * @param specification
     * @return
     */
    PageResult search(Integer page, Integer rows, Specification specification);

    /**
     * 新增规格数据
     * @param specificationVo
     */
    void add(SpecificationVo specificationVo);
}
