package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import org.springframework.data.mongodb.repository.MongoRepository;

//MongoRepository<T, ID> T: 查询的对象，ID对象的ID的类型
public interface CmsPageRepository extends MongoRepository<CmsPage, String> {

    //根据页面名称查询
    CmsPage findByPageName(String pageName);

    CmsPage findByPageNameAndSiteIdAndPageWebPath(String pageName,String siteId,String pageWebPath);
}
