package com.jiang.duckso.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *DSL 复杂查询
 */
@RestController
@RequestMapping("/blogQuery")
public class DynamicQueryController {


    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;


    @GetMapping("/get")
    public void  getQuery(){

    }

}
