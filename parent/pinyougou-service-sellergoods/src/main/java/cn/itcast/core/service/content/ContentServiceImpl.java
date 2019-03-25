package cn.itcast.core.service.content;

import cn.itcast.core.dao.ad.ContentDao;
import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.ad.ContentQuery;
import cn.itcast.core.pojo.entity.PageResult;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {

	@Resource
	private RedisTemplate redisTemplate;

	@Resource
	private ContentDao contentDao;

	/**
	 * 查询所有广告
	 * @return
	 */
	@Override
	public List<Content> findAll() {
		List<Content> list = contentDao.selectByExample(null);
		return list;
	}

	/**
	 * 查询所有广告的分页信息
	 * @param content
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@Override
	public PageResult findPage(Content content, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<Content> page = (Page<Content>)contentDao.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 新增广告
	 * @param content
	 */
	@Override
	public void add(Content content) {
		//同步缓存，清空缓存
		clearCache(content.getCategoryId());
		contentDao.insertSelective(content);
	}


	/**
	 * 更新广告
	 * @param content
	 */
	@Override
	public void edit(Content content) {
		//注意更新广告的时候，如果更新了广告分类，就需要把旧的分类id缓存都删掉
		Long oldCategoryId = contentDao.selectByPrimaryKey(content.getId()).getCategoryId();
		Long newCategoryId = content.getCategoryId();
		//注意分类是否变更的判断要放在更新方法之前，否则新旧ID永远都一致
		if (oldCategoryId!=newCategoryId){
			//如果分类修改了
			clearCache(oldCategoryId);
			clearCache(newCategoryId);
		}else {
			clearCache(newCategoryId);
		}

		contentDao.updateByPrimaryKeySelective(content);
	}

	/**
	 * 根据id查询某个广告
	 * @param id
	 * @return
	 */
	@Override
	public Content findOne(Long id) {
		Content content = contentDao.selectByPrimaryKey(id);
		return content;
	}

	/**
	 * 批量删除广告
	 * @param ids
	 */
	@Override
	public void delAll(Long[] ids) {
		if(ids != null){
			for(Long id : ids){
				clearCache(contentDao.selectByPrimaryKey(id).getCategoryId());
				contentDao.deleteByPrimaryKey(id);
			}
		}
	}

	/**
	 * 根据广告类别id查询某个广告位的广告
	 *
	 * @param categoryId
	 * @return
	 */
	@Override
	public List<Content> findByCategoryId(Long categoryId) {

		//先从缓存查询
		List<Content> contentList = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);
		if(contentList==null){
			//缓存找不到，去数据库查
			//但是当并发访问一些不在缓存的数据会产生缓存穿透
			/*
			解决方案：
			1、首先让避免访问并发（加锁）；
			2、然后进行二次校验
			（即：第一次访问执行完同步代码块之后，缓存已经有数据，第二次访问进入同步代码块就应该先查缓存有没有数据）
			 */
			synchronized (this){
				//二次校验
				contentList= (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);
				if (contentList==null){
					//二次校验缓存还是没有数据才访问数据库
					ContentQuery contentQuery = new ContentQuery();
					ContentQuery.Criteria criteria = contentQuery.createCriteria();
					criteria.andCategoryIdEqualTo(categoryId);
					contentList=contentDao.selectByExample(contentQuery);
					//数据库中查到后放入缓存
					redisTemplate.boundHashOps("content").put(categoryId,contentList);
				}
			}
		}
		return contentList;
	}

	//根据广告分类id清空缓存
	private void clearCache(Long categoryId) {
		redisTemplate.boundHashOps("content").delete(categoryId);
	}
}
