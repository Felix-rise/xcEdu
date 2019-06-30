package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepositroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConfigService {

    @Autowired
    CmsConfigRepositroy cmsConfigRepositroy;

    public CmsConfig getModel(String id) {
        Optional<CmsConfig> byId = cmsConfigRepositroy.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        }
        return null;
    }
}
