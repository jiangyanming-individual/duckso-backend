package com.jiang.duckso.job.once;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jiang.duckso.common.ErrorCode;
import com.jiang.duckso.exception.BusinessException;
import com.jiang.duckso.model.entity.Post;
import com.jiang.duckso.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 每次spring boot 启动都会执行这个，单次任务：
 */

//@Component 取消注释会每次启动springboot 都会执行这个任务：
@Slf4j
public class FetchInitPostList implements CommandLineRunner {

    @Resource
    private PostService postService;


    @Override
    public void run(String... args) throws Exception {

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
        if (b) {
            log.info("获取初始化帖子列表成功, 条数 = {}", postList.size());
        } else {
            log.error("获取初始化帖子列表失败");
        }
    }
}
