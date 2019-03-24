package cn.itcast.core.service.content;

import java.util.List;

import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.entity.PageResult;


public interface ContentService {

	public List<Content> findAll();

	public PageResult findPage(Content content, Integer pageNum, Integer pageSize);

	public void add(Content content);

	public void edit(Content content);

	public Content findOne(Long id);

	public void delAll(Long[] ids);

	/**
	 * 根据广告类别id查询某个广告位的广告
	 * @param categoryId
	 * @return
	 */
	List<Content> findByCategoryId(Long categoryId);
}
