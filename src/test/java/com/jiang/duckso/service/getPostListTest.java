package com.jiang.duckso.service;


import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jiang.duckso.common.ErrorCode;
import com.jiang.duckso.exception.BusinessException;
import com.jiang.duckso.model.entity.Post;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.json.Json;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class getPostListTest {


    @Resource
    private PostService postService;

    @Test
    public void getPostList() {

        String json = "{\"current\":1,\"pageSize\":6,\"category\":\"文章\",\"sortField\":\"viewNum\",\"sortOrder\":\"descend\",\"reviewStatus\":1,\"hiddenContent\":true}";
        String url = "https://api.code-nav.cn/api/post/list/page/vo";
        String result = HttpRequest.post(url)
                .body(json)
                .execute().
                body();
        System.out.println(result);
        //json 转对象；
        Map<String, Object> dataMap = JSONUtil.toBean(result, Map.class);
        JSONObject data = (JSONObject) dataMap.get("data");
        JSONArray records = (JSONArray) data.get("records");

        List<Post> postList = new ArrayList<>();
        //一个对象：
        for (Object record : records) {
            JSONObject tempRecord = JSONUtil.parseObj(record);
            Post post = new Post();
            //todo 判空操作：
            String title = tempRecord.getStr("title");
            String content = tempRecord.getStr("content");
            post.setTitle(title);
            post.setContent(content);
            //数组
            JSONArray tags = JSONUtil.parseArray(tempRecord.get("tags"));
            //转List
            List<String> tagList = JSONUtil.toList(tags, String.class);
            //转为List 转str
            post.setTags(JSONUtil.toJsonStr(tagList));
            post.setUserId(1L);
            postList.add(post);
        }
//        System.out.println(postList);

        //插入到数据库
        boolean b = postService.saveBatch(postList);
        if (!b) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "插入数据库失败");
        }
    }
}
