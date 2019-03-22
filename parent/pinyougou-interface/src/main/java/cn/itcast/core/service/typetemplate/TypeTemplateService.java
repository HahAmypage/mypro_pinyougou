package cn.itcast.core.service.typetemplate;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.template.TypeTemplate;
import com.alibaba.dubbo.config.annotation.Service;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService {

    /**
     * 查询模板信息
     */
    PageResult search(Integer page, Integer rows, TypeTemplate typeTemplate);

    /**
     * 添加模板
     * @param typeTemplate
     */
    void add(TypeTemplate typeTemplate);

    /**
     * 修改回显-根据id查询
     * @param id
     * @return
     */
    TypeTemplate findOne(Long id);

    /**
     * 保存修改
     * @param typeTemplate
     */
    void update(TypeTemplate typeTemplate);

    /**
     * 删除模板
     * @param ids
     */
    void delete(Long[] ids);

    /**
     * 查询数据--初始化下拉框
     * @return
     */
    List<TypeTemplate> findAll();
}
