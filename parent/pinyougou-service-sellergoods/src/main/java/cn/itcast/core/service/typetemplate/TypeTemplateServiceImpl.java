package cn.itcast.core.service.typetemplate;

import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Resource
    private TypeTemplateDao typeTemplateDao;
    /**
     * 查询模板信息
     *
     * @param page
     * @param rows
     * @param typeTemplate
     */
    @Override
    public PageResult search(Integer page, Integer rows, TypeTemplate typeTemplate) {
        //设置分页条件
        PageHelper.startPage(page,rows);
        //设置查询条件
        TypeTemplateQuery typeTemplateQuery = new TypeTemplateQuery();
        if (typeTemplate.getName()!=null&& !"".equals(typeTemplate.getName().trim())){
            typeTemplateQuery.createCriteria().andNameLike(typeTemplate.getName());
        }
        //查询
        Page<TypeTemplate> typeTemplatePage= (Page<TypeTemplate>) typeTemplateDao.selectByExample(typeTemplateQuery);
        //封装结果集
        return new PageResult(typeTemplatePage.getTotal(),typeTemplatePage.getResult());
    }
}
