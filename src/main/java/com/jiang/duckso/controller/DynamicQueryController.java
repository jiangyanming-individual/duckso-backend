package com.jiang.duckso.controller;

import com.jiang.duckso.model.dto.es.Blog;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.ParsedMax;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.AggregationsContainer;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilterBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *DSL 复杂查询
 */
@RestController
@RequestMapping("/blogQuery")
public class DynamicQueryController {


    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;


    /**
     * 简单查询
     */
    @GetMapping("/get")
    public List<Blog> simpleQuery(String title, String content){

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(title)){
            boolQueryBuilder.must(QueryBuilders.matchQuery("title",title));
        }
        if (StringUtils.isNotBlank(content)){
            boolQueryBuilder.must(QueryBuilders.matchQuery("content",content));
        }
        NativeSearchQuery nativeSearchQuery=new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
        SearchHits<Blog> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, Blog.class);
        List<Blog> blogs = new ArrayList<>();
        for (SearchHit<Blog> searchHit : searchHits) {
            // 获取SearChHit的内容：
            blogs.add(searchHit.getContent());
        }
        return blogs;
    }

    /**
     * 分页
     */

    @GetMapping("pageAndSort")
    public Page<Blog> pageAndSort(String title, String author) {
        // 分页
        PageRequest pageRequest = PageRequest.of(0, 2);
        // 布尔查询：
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(title)) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("title", title));
        }
        if (StringUtils.isNotBlank(author)) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("author", author));
        }
        NativeSearchQuery nativeSearchQuery=new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withPageable(pageRequest) // 分页
                .withSorts(SortBuilders.fieldSort("createTime").order(SortOrder.DESC)) // 按创建时间字段进行降序
                .build();
        SearchHits<Blog> search = elasticsearchRestTemplate.search(nativeSearchQuery, Blog.class);
        List<Blog> blogList=new ArrayList<>();
        for (SearchHit<Blog> blogSearchHit : search) {
            blogList.add(blogSearchHit.getContent());
        }
        // 分页：
        return new PageImpl<>(blogList,pageRequest, search.getTotalHits());
    }


    /**
     * 去重
     */


    @GetMapping("/collapse")
    public Page<Blog> collapse(String title, String author) {
        // 分页
        PageRequest pageRequest = PageRequest.of(0, 2);
        // 布尔查询：
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(title)) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("title", title));
        }
        if (StringUtils.isNotBlank(author)) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("author", author));
        }
        NativeSearchQuery nativeSearchQuery=new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withPageable(pageRequest) // 分页
                .withSorts(SortBuilders.fieldSort("createTime").order(SortOrder.DESC)) // 按创建时间字段进行降序
                .withCollapseField("author.keywords") //去重字段不能是text, 必须是keyword
                .build();
        SearchHits<Blog> search = elasticsearchRestTemplate.search(nativeSearchQuery, Blog.class);
        List<Blog> blogList=new ArrayList<>();
        for (SearchHit<Blog> blogSearchHit : search) {
            blogList.add(blogSearchHit.getContent());
        }
        // 分页：
        return new PageImpl<>(blogList,pageRequest, search.getTotalHits());
    }

    /**
     * 聚合
     */
    @ApiOperation("聚合")
    @GetMapping("aggregation")
    public Map<String, Long> aggregation() {
        // 复杂查询
        NativeSearchQueryBuilder query = new NativeSearchQueryBuilder();
        //布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("title", "java"));
        query.withQuery(boolQueryBuilder);

        query.addAggregation(AggregationBuilders
                .terms("per_count") // 聚合的名字为per_count
                .field("author.keyword")); // 以author的keyword 进行聚合
        // 不需要获取source结果集，在aggregation里可以获取结果
        query.withSourceFilter(new FetchSourceFilterBuilder().build());

        SearchHits<Blog> searchHits = elasticsearchRestTemplate.search(query.build(), Blog.class);
        AggregationsContainer<?> aggregationsContainer = searchHits.getAggregations();
        Aggregations aggregations = (Aggregations) aggregationsContainer.aggregations();
        assert aggregations != null;
        //因为结果为字符串类型 所以用ParsedStringTerms。其他还有ParsedLongTerms、ParsedDoubleTerms等
        ParsedStringTerms per_count = aggregations.get("per_count");
        Map<String, Long> map = new HashMap<>();
        for (Terms.Bucket bucket : per_count.getBuckets()) {
            map.put(bucket.getKeyAsString(), bucket.getDocCount());
        }
        return map;
    }



    /**
     * 嵌套
     */


    @ApiOperation("嵌套聚合")
    @GetMapping("subAggregation")
    public Map<String, Map<String, Object>> subAggregation() {
        // 复杂查询
        NativeSearchQueryBuilder query = new NativeSearchQueryBuilder();
        //布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("title", "java"));
        query.withQuery(boolQueryBuilder);
        // 聚合条件：
        query.addAggregation(AggregationBuilders
                .terms("per_count") // 聚合的名字为per_count
                .field("author.keyword") // 以author的keyword 进行聚合
                .subAggregation( //子聚合以最后的创建时间
                 AggregationBuilders.max("last_create_time")
                                .field("createTime")));
        // 不需要获取source结果集，在aggregation里可以获取结果
        query.withSourceFilter(new FetchSourceFilterBuilder().build());
        // 搜索结果
        SearchHits<Blog> searchHits = elasticsearchRestTemplate.search(query.build(), Blog.class);
        // 整理聚合结果：
        AggregationsContainer<?> aggregationsContainer = searchHits.getAggregations();
        Aggregations aggregations = (Aggregations) aggregationsContainer.aggregations();
        assert aggregations != null;
        //因为结果为字符串类型 所以用ParsedStringTerms。其他还有ParsedLongTerms、ParsedDoubleTerms等
        ParsedStringTerms per_count = aggregations.get("per_count");
        Map<String, Map<String, Object>> map = new HashMap<>();
        for (Terms.Bucket bucket : per_count.getBuckets()) {
            // 数量：
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put("docCount",bucket.getDocCount());
            // 子聚合
            ParsedMax lastCreateTime = bucket.getAggregations().get("last_create_time");
            objectMap.put("lastCreateTime",lastCreateTime.getValueAsString());
            map.put(bucket.getKeyAsString(),objectMap);
        }
        return map;
    }
}


