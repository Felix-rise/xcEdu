package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.framework.model.response.QueryResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    SysDicthinaryRespository sysDicthinaryRespository;

    @Test
    public void testFindSys(){
        SysDictionary byDType = sysDicthinaryRespository.findByDType("200");
        System.out.println(byDType);
    }

    @Test
    public void testFindAll() {
        List<CmsPage> all = cmsPageRepository.findAll();
        System.out.println(all);
    }

    //分页
    @Test
    public void testPage() {
        //分页参数
        int page = 0;//从0开始
        int size = 10;
        Pageable pageable = PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        System.out.println(all);
    }

    //修改
    @Test
    public void testUpdateS(){
        //返回Optional对象为jdk1.8的判断非空标准的容器对象
        Optional<CmsPage> optional = cmsPageRepository.findById("5af942190e661827d8e2f5e3");
        if(optional.isPresent()){
            CmsPage cmsPage = optional.get();
            cmsPage.setPageType("2");
            CmsPage save = cmsPageRepository.save(cmsPage);
            System.out.println(save);
        }
    }

    //自定义方法
    @Test
    public void testFindByPageName(){
        CmsPage byPageName = cmsPageRepository.findByPageName("preview_4028e58161bd3b380161bd3bcd2f0000.html");
        System.out.println(byPageName);
    }


    //自定义条件查询
    @Test
    public void testFindByExample(){
        //条件匹配器
        CmsPage cmsPage = new CmsPage();
//        cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");
//        cmsPage.setPageName("index.html");
        cmsPage.setPageAliase("轮");
        //根据条件匹配
        ExampleMatcher exampleMatcher =ExampleMatcher.matching()
                        .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);
        //分页
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        System.out.println(all);
    }

}
