package com.jiang.duckso.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiang.duckso.model.dto.post.PostQueryRequest;
import com.jiang.duckso.model.vo.PostVO;
import com.jiang.duckso.service.PostService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@Service
public class PostDataSource implements DataSource{

    @Resource
    PostService postService;


    @Override
    public Page<PostVO> doSearch(String searchText, int current, int pageSize) {

        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setSearchText(searchText);
        postQueryRequest.setCurrent(current);
        postQueryRequest.setPageSize(pageSize);
        // 获取上下文request对象：
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return postService.listPostVOByPage(postQueryRequest, request);
    }
}
