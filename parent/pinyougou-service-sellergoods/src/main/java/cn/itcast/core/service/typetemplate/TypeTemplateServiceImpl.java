package cn.itcast.core.service.typetemplate;

import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import javax.annotation.Resource;

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
        typeTemplateQuery.setOrderByClause("id desc");
        //查询
        Page<TypeTemplate> typeTemplatePage= (Page<TypeTemplate>) typeTemplateDao.selectByExample(typeTemplateQuery);
        //封装结果集
        return new PageResult(typeTemplatePage.getTotal(),typeTemplatePage.getResult());
    }

    /**
     * 添加模板
     *
     * @param typeTemplate
     */
    @Override
    public void add(TypeTemplate typeTemplate) {
        typeTemplateDao.insertSelective(typeTemplate);
    }

    /**
     * 修改回显-根据id查询
     *
     * @param id
     * @return
     */
    @Override
    public TypeTemplate findOne(Long id) {
        return typeTemplateDao.selectByPrimaryKey(id);
    }

    /**
     * 保存修改
     *
     * @param typeTemplate
     */
    @Override
    public void update(TypeTemplate typeTemplate) {
        typeTemplateDao.updateByPrimaryKeySelective(typeTemplate);
    }

    /**
     * 删除模板
     *
     * @param ids
     */
    @Override
    public void delete(Long[] ids) {
        if (ids!=null&&ids.length>0){
//            for (Long id:ids){
//                typeTemplateDao.deleteByPrimaryKey(id);
//            }
            typeTemplateDao.deleteBatch(ids);
        }
    }
}
