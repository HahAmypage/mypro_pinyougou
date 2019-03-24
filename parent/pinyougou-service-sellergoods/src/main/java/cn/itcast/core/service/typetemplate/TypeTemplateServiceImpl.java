package cn.itcast.core.service.typetemplate;

import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Resource
    private TypeTemplateDao typeTemplateDao;

    @Resource
    private SpecificationOptionDao specificationOptionDao;
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
    @Transactional
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
    @Transactional
    @Override
    public void update(TypeTemplate typeTemplate) {
        typeTemplateDao.updateByPrimaryKeySelective(typeTemplate);
    }

    /**
     * 删除模板
     *
     * @param ids
     */
    @Transactional
    @Override
    public void delete(Long[] ids) {
        if (ids!=null&&ids.length>0){
//            for (Long id:ids){
//                typeTemplateDao.deleteByPrimaryKey(id);
//            }
            typeTemplateDao.deleteBatch(ids);
        }
    }

    /**
     * 查询数据--初始化下拉框
     *
     * @return
     */
    @Override
    public List<TypeTemplate> findAll() {
        return typeTemplateDao.selectByExample(null);
    }

    /**
     * 根据模板id查找规格选项
     *
     * @param id
     * @return
     */
    @Override
    public List<Map> findBySpecList(Long id) {
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        //[{"id":16,"text":"TCL"},{"id":13,"text":"长虹"},{"id":14,"text":"海尔"}]
        String specIds = typeTemplate.getSpecIds();
        List<Map> specList = JSON.parseArray(specIds,Map.class);
        if (specList!=null&& specList.size()>0){
            for (Map map:specList){
                String object = map.get("id").toString();
                Long specId = Long.parseLong(object);
                SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
                specificationOptionQuery.createCriteria().andSpecIdEqualTo(specId);
                List<SpecificationOption> specificationOptions = specificationOptionDao.selectByExample(specificationOptionQuery);

                map.put("options",specificationOptions);
            }

            return specList;
        }
        return null;
    }
}
