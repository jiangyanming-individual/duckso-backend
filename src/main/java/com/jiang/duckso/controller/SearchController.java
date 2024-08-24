package com.jiang.duckso.controller;

import com.jiang.duckso.common.BaseResponse;
import com.jiang.duckso.common.ResultUtils;
import com.jiang.duckso.manager.SearchFacadeManager;
import com.jiang.duckso.model.dto.search.SearchQueryRequest;
import com.jiang.duckso.model.vo.SearchVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * 聚合接口，实现使用多线程异步的方法
 */

@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {


    /**
     * 门面模式的使用
     */
    @Resource
    SearchFacadeManager facadeManager;

    /**
     * 搜索所有数据源 和按类型搜索数据
     *
     * @param searchQueryRequest
     * @return
     */
    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchQueryRequest searchQueryRequest, HttpServletRequest request) {
        SearchVO searchVO = facadeManager.searchAll(searchQueryRequest, request);
        return ResultUtils.success(searchVO);
    }
}
