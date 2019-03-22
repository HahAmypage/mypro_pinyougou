package cn.itcast.core.controller.goods;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.service.goods.GoodsDescService;
import cn.itcast.core.vo.GoodsVo;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.itcast.core.service.goods.GoodsService;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @Reference
    private GoodsDescService goodsDescService;

    @RequestMapping("/add.do")
    public Result add(@RequestBody GoodsVo goodsVo){
        try {
            goodsService.add(goodsVo);
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }
}
