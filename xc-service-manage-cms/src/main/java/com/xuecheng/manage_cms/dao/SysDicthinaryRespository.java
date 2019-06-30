package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Felix
 * @version 1.0
 * @date 2019/6/20 14:49
 */
@Repository
public interface SysDicthinaryRespository extends MongoRepository<SysDictionary, String> {

    //根据字典分类查询字典信息
    public SysDictionary findByDType(String dType);
}
