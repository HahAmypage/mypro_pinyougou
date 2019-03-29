package cn.itcast.core.service.staticpage;

/**
 * @ClassName StaticPageService
 * @Description 生成商品静态详情页
 * @Author chenyingxin
 * @Date 15:16 2019/3/28
 * @Version 2.1
 */
public interface StaticPageService {

    /**
     * @Author chenyingxin
     * @Descristion 生成商品静态详情页
     * @Date 15:17 2019/3/28
     * @param id 商品id
     * @return void
     */
    void getHtml(Long id);
}
