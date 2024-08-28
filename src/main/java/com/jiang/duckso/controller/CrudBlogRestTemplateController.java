package com.jiang.duckso.controller;


import com.jiang.duckso.model.dto.es.Blog;
import io.swagger.annotations.Api;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.elasticsearch.core.query.UpdateResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/blog")
public class CrudBlogRestTemplateController {


    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 插入一条文档：
     * @param blog
     * @return
     */
    @PostMapping("/add")
    public Blog addDocument(@RequestBody Blog blog){
        Blog insertBlog = elasticsearchRestTemplate.save(blog);
        return insertBlog;
    }

    /**
     * 添加多个文档
     * @param count
     * @return
     */
    @PostMapping("addDocuments")
    public Object addDocuments(Integer count) {
        List<Blog> blogs = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Long id = (long)i;
            Blog blog = new Blog();
            blog.setBlogId(id);
            blog.setTitle("Spring Data ElasticSearch学习教程" + id);
            blog.setContent("这是添加单个文档的实例" + id);
            blog.setAuthor("Tony");
            blog.setCategory("ElasticSearch");
            blog.setCreateTime(new Date());
            blog.setStatus(1);
            blog.setSerialNum(id.toString());
            blogs.add(blog);
        }
        // 插入集合
        return elasticsearchRestTemplate.save(blogs);
    }

    /**
     * 修改文档数据
     * @return
     */
    @PostMapping("/editDocument")
    public Blog editDocument() {
        Long id = 1L;
        Blog blog = new Blog();
        blog.setBlogId(id);
        blog.setTitle("Spring Data ElasticSearch学习教程" + id);
        blog.setContent("修改文档的数据" + id);

        // 如果id存在默认会覆盖：
       return elasticsearchRestTemplate.save(blog);
    }


    /**
     * 更新部分文档
     * @return
     */
    @PostMapping("/editDocumentPart")
    public UpdateResponse editDocumentPart() {
        Long id = 1L;
        Blog blog = new Blog();
        blog.setBlogId(id);
        blog.setTitle("Spring Data ElasticSearch学习教程" + id);
        blog.setContent("这是修改单个文档的实例" + id);
        //文档：
        Document document = Document.create();
        document.put("title", "你被修改部分" + id);
        document.put("content", "你被修改部分" + id);
        UpdateQuery updateQuery= UpdateQuery
                .builder(Long.toString(id))
                .withDocument(document).build();

        // 更新部分的数据
        return elasticsearchRestTemplate.update(updateQuery, IndexCoordinates.of("blog"));
    }


    /**
     * 批量修改文档：
     * @return
     */
    @PostMapping("/editManyDocumentPart")
    public void  editManyDocumentPart(int count) {
        List<UpdateQuery> updateQueryList = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            long id = (long) i;
            Document document = Document.create();
            document.put("title","批量更新后的数据" + i);
            document.put("content","批量更新后的内容"+ i);
            UpdateQuery updateQuery= UpdateQuery.builder(Long.toString(id)).withDocument(document).build();
            updateQueryList.add(updateQuery);
        }
        elasticsearchRestTemplate.bulkUpdate(updateQueryList,IndexCoordinates.of("blog"));
    }


    @GetMapping("/findById")
    public Blog getDocument(Long id){
        return elasticsearchRestTemplate.get(id.toString(),Blog.class,IndexCoordinates.of("blog"));
    }

    /**
     * 根据内容删除文档
     * @param title
     */
    @PostMapping("deleteDocumentByQuery")
    public void deleteDocumentByQuery(String title) {

        // 精确匹配：matchQuery
        NativeSearchQuery nativeSearchQuery=new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("title",title))
                .build();
        elasticsearchRestTemplate.delete(nativeSearchQuery, Blog.class,IndexCoordinates.of("blog"));
    }

    @PostMapping("deleteDocumentAll")
    public void deleteDocumentAll() {
        // 匹配所有的数据：matchAllQuery
        NativeSearchQuery nativeSearchQuery=new NativeSearchQueryBuilder().
                withQuery(QueryBuilders.matchAllQuery())
                .build();
        elasticsearchRestTemplate.delete(nativeSearchQuery,Blog.class,IndexCoordinates.of("blog"));
    }

}
