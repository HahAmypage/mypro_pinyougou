package cn.itcast.core.service.typetemplate;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.template.TypeTemplate;
import com.alibaba.dubbo.config.annotation.Service;

public interface TypeTemplateService {

    /**
     * 查询模板信息
     */
    PageResult search(Integer page, Integer rows, TypeTemplate typeTemplate);
}
