package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

//MongoRepository<T, ID> T: 查询的对象，ID对象的ID的类型
public interface CmsTemplateRepository extends MongoRepository<CmsTemplate,String> {

}
