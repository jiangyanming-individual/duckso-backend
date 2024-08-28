package com.jiang.duckso.esrepository;


import com.jiang.duckso.model.dto.es.Books;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import javax.annotation.Resource;

@SpringBootTest
public class BookEsTest {


    @Autowired
    BookRepository bookRepository;

    //父类
    @Autowired
    public ElasticsearchOperations elasticsearchOperations;

    // 基于spring restTemplate模版
    @Resource
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 创建索引, 以及表的结构：
     */
    @Test
    public void createIndex(){
        boolean b = elasticsearchRestTemplate.indexOps(Books.class).create();
        System.out.println(b);
    }
    /**
     * 插入操作
     */
    @Test
    public void addTest(){
        Books books = new Books();
        books.setId(2L);
        books.setName("水浒传");
        books.setSummary("梁山好汉的故事");
        books.setPrice(100);
        bookRepository.save(books);
    }

    // 删除书籍
    @Test
    void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    // 更新书籍
    @Test
    void updateBook() {

        Books books = new Books();
        books.setId(1L);
        books.setName("西游记");
        books.setSummary("现在黑神话悟空出来了");
        books.setPrice(222);
        bookRepository.save(books);
    }

    // 查询书籍
    @Test
    void findBook() {
        Books books = bookRepository.findById(1L).orElse(null);
        System.out.println(books);
    }
}
