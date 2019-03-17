package cn.itcast.core.controller.brand;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.service.brand.BrandService;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll.do")
    public List<Brand> findAll(){
        return brandService.findAll();
    }

    @RequestMapping("/findByPage.do")
    public PageResult findByPage(Integer pageNo,Integer pageSize){
        return brandService.findByPage(pageNo,pageSize);
    }

    @RequestMapping("/search.do")
    public PageResult search(Integer pageNo, Integer pageSize, @RequestBody Brand brand){
        return brandService.search(pageNo,pageSize,brand);
    }
}
