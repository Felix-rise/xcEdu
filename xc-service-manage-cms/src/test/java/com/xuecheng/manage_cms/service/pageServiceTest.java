package com.xuecheng.manage_cms.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class pageServiceTest {

    @Autowired
    PageService pageService;

    @Test
    public void testGeneratePageHtml(){
        String pageHtml = pageService.getPageHtml("5cf004557bf3236168e3bef5");
        System.out.println(pageHtml);
    }
}
