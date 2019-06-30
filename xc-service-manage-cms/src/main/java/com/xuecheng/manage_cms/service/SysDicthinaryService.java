package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.framework.domain.system.response.SystemCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.manage_cms.dao.SysDicthinaryRespository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

/**
 * @author Felix
 * @version 1.0
 * @date 2019/6/20 14:48
 */
@Service
public class SysDicthinaryService {

    @Autowired
    SysDicthinaryRespository sysDicthinaryRespository;

    //根据字典分类type查询字典信息
    public SysDictionary findDictionaryByType(String type) {
        if (StringUtils.isEmpty(type)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }

        SysDictionary DType = sysDicthinaryRespository.findByDType(type);
        if (DType == null) {
            ExceptionCast.cast(SystemCode.SYSTEM_DICTIONARY_NOTEXISTS);
        }

        return DType;
    }
}
