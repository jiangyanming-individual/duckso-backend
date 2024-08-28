package com.jiang.duckso.esrepository;


import com.jiang.duckso.model.dto.es.Blog;
import com.jiang.duckso.service.impl.BlogServiceImpl;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import javax.annotation.Resource;

@SpringBootTest
public class BlogTest {



    @Resource
    private BlogServiceImpl blogService;

    @Test
    public void creatIndex(){
        blogService.createIndex();
    }

    @Test
    public void addIndex(){

        Blog blog = new Blog();

        blog.setTitle("My First Blog");
        blog.setContent("This is the content of my first blog.");
        blog.setAuthor("John Doe");
        blogService.insertIndex(blog);
    }
}
