package cn.itcast.core.controller.seller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.seller.SellerService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference
    private SellerService sellerService;

    /**
     * 查询商家信息
     * @param page
     * @param rows
     * @param seller
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody Seller seller){
        return sellerService.search(page,rows,seller);
    }

    /**
     * 根据id查询商家
     * @param id
     * @return
     */
    @RequestMapping("/findOne.do")
    public Seller findOne(String id){
        return sellerService.findOne(id);
    }

    /**
     * 更新商家审核状态
     * @param sellerId
     * @param status
     * @return
     */
    @RequestMapping("/updateStatus.do")
    public Result updateStatus(String sellerId, String status){
        try {
            sellerService.updateStatus(sellerId,status);
            return new Result(true,"操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败");
        }
    }
}
