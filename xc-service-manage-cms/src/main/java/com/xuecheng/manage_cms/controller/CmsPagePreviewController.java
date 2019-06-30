package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.PageService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

@Controller
public class CmsPagePreviewController extends BaseController {

    @Autowired
    PageService pageService;

    //页面预览
    @GetMapping(value = "/cms/preview/{pageId}")
    public void preview(@PathVariable("pageId") String pageId) {
        //执行静态化
        String html = pageService.getPageHtml(pageId);

        StringBuffer requestURL = request.getRequestURL();

        if (StringUtils.isNotEmpty(html)) {
            try {
                //通过response对象将内容输出给页面
                response.setHeader("Content‐type", "text/html;charset=utf‐8");
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(html.getBytes("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
