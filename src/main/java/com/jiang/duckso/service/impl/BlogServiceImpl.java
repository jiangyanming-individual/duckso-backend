package com.jiang.duckso.service.impl;


import com.jiang.duckso.model.dto.es.Blog;
import com.jiang.duckso.model.dto.es.Books;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 * 博客文档service实现类：
 */
@Service
@Slf4j
public class BlogServiceImpl {

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 创建索引：
     */
    public void createIndex(){

        elasticsearchRestTemplate.indexOps(Blog.class).putMapping();

    }

    /**
     * 添加博客索引：
     */
    public void insertIndex(Blog blog){
        elasticsearchRestTemplate.save(blog);

    }


    public void deleteIndex(Blog blog){
        elasticsearchRestTemplate.delete(blog);
    }
}
