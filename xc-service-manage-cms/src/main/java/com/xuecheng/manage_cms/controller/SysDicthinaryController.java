package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.system.SysDicthinaryControllerApi;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.service.SysDicthinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Felix
 * @version 1.0
 * @date 2019/6/20 14:46
 */
@RestController
@RequestMapping("/sys/dictionary")
public class SysDicthinaryController implements SysDicthinaryControllerApi {

    @Autowired
    SysDicthinaryService sysDicthinaryService;


    //根据字典分类id查询字典信息
    @Override
    @GetMapping("/get/{type}")
    public SysDictionary getByType(@PathVariable("type") String type) {
        return sysDicthinaryService.findDictionaryByType(type);
    }
}
