package cn.itcast.core.service.specification;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.vo.SpecificationVo;
import javafx.util.converter.LocalDateStringConverter;

import java.util.List;
import java.util.Map;

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

    /**
     * 根据id查询
     * @param id
     * @return
     */
    SpecificationVo findOne(Long id);

    /**
     * 修改规格和规格选项
     * @param specificationVo
     */
    void update(SpecificationVo specificationVo);

    /**
     * //删除规格（同时删除规格选项）
     * @param ids
     */
    void delete(Long[] ids);

    /**
     * 新增模板-下拉框初始化
     * @return
     */
    List<Map<String,String>> selectOptionList();
}
