package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

//MongoRepository<T, ID> T: 查询的对象，ID对象的ID的类型
public interface CmsSiteRepository extends MongoRepository<CmsSite,String> {

}
