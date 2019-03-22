package cn.itcast.core.service.seller;

import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.pojo.seller.SellerQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class SellerServiceImpl implements SellerService {

    @Resource
    private SellerDao sellerDao;
    /**
     * 注册用户
     *
     * @param seller
     */
    @Override
    public void add(Seller seller) {
        seller.setStatus("0");
        seller.setCreateTime(new Date());
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodePwd = bCryptPasswordEncoder.encode(seller.getPassword());
        seller.setPassword(encodePwd);
        sellerDao.insertSelective(seller);
    }

    /**
     * 查询商家
     *
     * @param page
     * @param rows
     * @param seller
     * @return
     */
    @Override
    public PageResult search(Integer page, Integer rows, Seller seller) {
        //设置分页条件
        PageHelper.startPage(page,rows);
        //设置查询条件
        SellerQuery sellerQuery = new SellerQuery();
        SellerQuery.Criteria criteria = sellerQuery.createCriteria();
        if (seller.getName()!=null&&!"".equals(seller.getName().trim())){
            criteria.andNameLike("%"+seller.getName().trim()+"%");
        }
        if (seller.getNickName()!=null&&!"".equals(seller.getNickName().trim())){
            criteria.andNickNameLike("%"+seller.getNickName().trim()+"%");
        }
        if(seller.getStatus()!=null){
            criteria.andStatusEqualTo(seller.getStatus().trim());
        }
        sellerQuery.setOrderByClause("create_time desc");
        //查询
        Page<Seller> sellerPage= (Page<Seller>) sellerDao.selectByExample(sellerQuery);
        //封装查询结果
        return new PageResult(sellerPage.getTotal(),sellerPage.getResult());
    }

    /**
     * 根据id查询商家
     *
     * @param id
     * @return
     */
    @Override
    public Seller findOne(String id) {
        return sellerDao.selectByPrimaryKey(id);
    }

    /**
     * 更新商家审核状态
     *
     * @param sellerId
     * @param status
     */
    @Override
    public void updateStatus(String sellerId, String status) {

        Seller seller = new Seller();
        seller.setSellerId(sellerId);
        seller.setStatus(status);
        sellerDao.updateByPrimaryKeySelective(seller);

    }
}
