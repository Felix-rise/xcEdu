package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class PageService {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    GridFSBucket gridFSBucket;

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    RabbitTemplate rabbitTemplate;


    /**
     * 页面列表分页查询
     *
     * @param page             当前页码
     * @param size             页面显示个数
     * @param queryPageRequest 查询条件
     * @return 页面列表
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {

        if (queryPageRequest == null) {
            queryPageRequest = new QueryPageRequest();
        }
        //条件查询对象
        CmsPage cmsPage = new CmsPage();
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteId())) {
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        if (StringUtils.isNotEmpty(queryPageRequest.getTemplateId())) {
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())) {
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }

        if (StringUtils.isNotEmpty(queryPageRequest.getPageName())) {
            cmsPage.setPageName(queryPageRequest.getPageName());
        }
        if (StringUtils.isNotEmpty(queryPageRequest.getPageType())) {
            cmsPage.setPageType(queryPageRequest.getPageType());
        }

        if (page <= 0) {
            page = 1;
        }
        page = page - 1;
        if (size <= 0) {
            size = 10;
        }
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("siteId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("templateId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("pageName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("pageType", ExampleMatcher.GenericPropertyMatchers.exact());
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        Pageable pageable = PageRequest.of(page, size);
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        QueryResult<CmsPage> queryResult = new QueryResult<>();
        queryResult.setList(all.getContent()); //数据列表
        queryResult.setTotal(all.getTotalElements()); //数据总记录数

        QueryResponseResult responseResult = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return responseResult;
    }

    /*//添加页面
    public CmsPageResult add(CmsPage cmsPage) {

        //校验页面是否存在，根据页面名称、站点Id、页面webpath查询
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());

        if (cmsPage1 == null) {
            //为了让主键自增，置空主键
            cmsPage.setPageId(null);
            cmsPageRepository.save(cmsPage);
            CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, cmsPage);
            return cmsPageResult;
        }
        return new CmsPageResult(CommonCode.FAIL, null);
    }*/

    //添加页面
    public CmsPageResult add(CmsPage cmsPage) {

        if (cmsPage == null) {
            //非法参数异常
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //校验页面是否存在，根据页面名称、站点Id、页面webpath查询
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());

        if (cmsPage1 != null) {
            //页面已经存在
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        //为了让主键在数据库中自增，置空主键
        cmsPage.setPageId(null);
        cmsPageRepository.save(cmsPage);
        CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, cmsPage);
        return cmsPageResult;
    }

    /**
     * 根据id查询cms页面
     *
     * @param id
     * @return
     */
    public CmsPage getById(String id) {
        Optional<CmsPage> byId = cmsPageRepository.findById(id);
        if (byId.isPresent()) {
            CmsPage cmsPage = byId.get();
            return cmsPage;
        }
        return null;
    }

    /**
     * 更新cms页面
     *
     * @param id
     * @param cmsPage 需要修改的页面信息
     * @return
     */
    public CmsPageResult update(String id, CmsPage cmsPage) {

        //从数据库获取到页面信息
        CmsPage page = this.getById(id);
        if (page != null) {
            //更新站点id
            page.setSiteId(cmsPage.getSiteId());
            //更新模板id
            page.setTemplateId(cmsPage.getTemplateId());
            //更新页面名称
            page.setPageName(cmsPage.getPageName());
            //更新页面别名
            page.setPageAliase(cmsPage.getPageAliase());
            //更新访问路径
            page.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径
            page.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //更新数据url
            page.setDataUrl(cmsPage.getDataUrl());
            //执行保存
            CmsPage savePage = cmsPageRepository.save(page);
            return new CmsPageResult(CommonCode.SUCCESS, savePage);
        }
        //返回更新失败
        return new CmsPageResult(CommonCode.FAIL, null);
    }

    public ResponseResult delete(String id) {
        CmsPage byId = this.getById(id);
        if (byId != null) {
            //删除页面
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }

        return new ResponseResult(CommonCode.FAIL);
    }


    /*
     * 页面静态化方法
     * 静态化程序获取页面的DataUrl
     * 静态化程序远程请求DataUrl获取数据模型。
     * 静态化程序获取页面的模板信息执行页面静态化
     * */
    public String getPageHtml(String pageId) {

        //1.获取数据模型
        Map model = this.getModelByPageId(pageId);
        if (ObjectUtils.isEmpty(model)) {
            //获取不到模型数据
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }

        //2.获取页面模板
        String template = getTemplateByPageId(pageId);
        if (StringUtils.isEmpty(template)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }

        //3.执行静态化
        String generateHtml = generateHtml(template, model);
        if (StringUtils.isEmpty(template)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_GETHTMLERROR);
        }

        return generateHtml;
    }


    //执行静态化
    private String generateHtml(String template, Map model) {

        //生成配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        //模板加载器
        StringTemplateLoader templateLoader = new StringTemplateLoader();
        //把模板内容放进去并取名为template
        templateLoader.putTemplate("template", template);
        //配置模板加载器
        configuration.setTemplateLoader(templateLoader);

        try {
            //获取模板
            Template template1 = configuration.getTemplate("template", "utf-8");
            //动态生成静态的html文件
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template1, model);
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    //获取页面模板
    private String getTemplateByPageId(String pageId) {
        //获取cms页面
        CmsPage cmsPage = this.getById(pageId);
        if (ObjectUtils.isEmpty(cmsPage)) {
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //根据page查询页面的模板id
        String templateId = cmsPage.getTemplateId();
        if (StringUtils.isEmpty(templateId)) {
            //模板信息不存在
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }

        //查询模板信息
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
        if (optional.isPresent()) {
            CmsTemplate cmsTemplate = optional.get();
            String templateFileId = cmsTemplate.getTemplateFileId();
            if (StringUtils.isEmpty(templateFileId)) {
                ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
            }
            //根据id查询文件
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            //打开下载流对象
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            //创建gridFsResource，用于获取流对象
            GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
            //获取流中的数据
            try {
                //获取模板内容
                String content = IOUtils.toString(gridFsResource.getInputStream(), "UTF-8");
                return content;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return null;
    }

    //获取页面模型数据
    private Map getModelByPageId(String pageId) {
        //获取cms页面
        CmsPage cmsPage = this.getById(pageId);
        if (ObjectUtils.isEmpty(cmsPage)) {
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }

        //获取dataUrl
        String dataUrl = cmsPage.getDataUrl();
        if (StringUtils.isEmpty(dataUrl)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }

        //通过restTemplate请求dataUrl获取数据
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();

        return body;
    }


    //页面发布
    public ResponseResult postPage(String pageId) {
        //执行页面静态化
        String pageHtml = this.getPageHtml(pageId);
        if (StringUtils.isEmpty(pageHtml)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        //将页面静态化的文件转储到GridFs中(保存静态化文件)
        CmsPage cmsPage = this.saveHtml(pageId, pageHtml);

        //向MQ发送消息
        this.sendPostPage(pageId);

        return new ResponseResult(CommonCode.SUCCESS);
    }

    //MQ发送页面发布消息
    private void sendPostPage(String pageId) {
        //查询页面
        CmsPage cmsPage = this.getById(pageId);
        if (Objects.isNull(cmsPage)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Map<String, String> msgMap = new HashMap();
        msgMap.put("pageId", pageId);
        String msg = JSON.toJSONString(msgMap);

        //获取站点Id，以站点Id为路由key
        String siteId = cmsPage.getSiteId();
        //发布消息
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE, siteId, msg);
    }

    //保存静态页面文件
    private CmsPage saveHtml(String pageId, String htmlContent) {
        //查询页面
        CmsPage cmsPage = this.getById(pageId);
        if (Objects.isNull(cmsPage)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //保存之前先删除
        String htmlFileId = cmsPage.getHtmlFileId();
        if (StringUtils.isNotEmpty(htmlFileId)) {
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(htmlFileId)));
        }
        InputStream inputStream = null;
        ObjectId objectId = null;
        try {
            //将htmlContent内容转成输入流
            inputStream = IOUtils.toInputStream(htmlContent, "utf-8");
            //将html文件内容保存到GridFS
            objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //将html文件id更新到cmsPage中
        cmsPage.setHtmlFileId(objectId.toHexString());
        cmsPageRepository.save(cmsPage);

        return cmsPage;
    }

    //保存cms页面信息，有则更新，没有则创建
    public CmsPageResult save(CmsPage cmsPage) {

        //根据三个属性查询唯一的cms页面
        CmsPage page = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (page != null) {
            return this.update(page.getPageId(), page);
        }
        return this.add(cmsPage);
    }
}
