package cn.itcast.core.service.specification;

import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationQuery;
import cn.itcast.core.vo.SpecificationVo;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SpecificationServiceImpl implements SpecificationService{

    @Resource
    private SpecificationDao specificationDao;

    @Resource
    private SpecificationOptionDao specificationOptionDao;

    @Override
    public PageResult search(Integer page, Integer rows, Specification specification) {
        //设置分页条件
        PageHelper.startPage(page,rows);
        //设置查询条件
        SpecificationQuery query = new SpecificationQuery();

        if (specification.getSpecName()!=null && !"".equals(specification.getSpecName().trim())){
            query.createCriteria().andSpecNameLike("%"+specification.getSpecName().trim()+"%");
        }

        query.setOrderByClause("id desc");
        //查询
        Page<Specification> specificationPage = (Page<Specification>) specificationDao.selectByExample(query);
        //封装查询结果
        return new PageResult(specificationPage.getTotal(),specificationPage.getResult());
    }

    /**
     * 新增规格数据
     *
     * @param specificationVo
     */
    @Override
    public void add(SpecificationVo specificationVo) {
        //保存规格数据，插入数据后要返回自增主键id
        Specification specification = specificationVo.getSpecification();
        specificationDao.insertSelective(specification);//返回自增id

        //保存规格选项数据
        List<SpecificationOption> specificationOptionList = specificationVo.getSpecificationOptionList();
        if (specificationOptionList!=null&&specificationOptionList.size()>0){
            for (SpecificationOption specificationOption:specificationOptionList){
                specificationOption.setSpecId(specification.getId());
                specificationOptionDao.insertSelective(specificationOption);
            }
        }
    }
}