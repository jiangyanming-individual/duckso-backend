package com.jiang.duckso.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiang.duckso.common.ErrorCode;
import com.jiang.duckso.datasource.*;
import com.jiang.duckso.exception.BusinessException;
import com.jiang.duckso.exception.ThrowUtils;
import com.jiang.duckso.model.dto.post.PostQueryRequest;
import com.jiang.duckso.model.dto.search.SearchQueryRequest;
import com.jiang.duckso.model.dto.user.UserQueryRequest;
import com.jiang.duckso.model.entity.Picture;
import com.jiang.duckso.model.enums.SearchTypeEnum;
import com.jiang.duckso.model.vo.PostVO;
import com.jiang.duckso.model.vo.SearchVO;
import com.jiang.duckso.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 门面模式的实现
 */
@Component
@Slf4j
public class SearchFacadeManager {

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private PostDataSource postDataSource;

    @Resource
    private DataSourceRegister dataSourceRegister;

    /**
     * 搜索所有数据源 和按类型搜索数据
     *
     * @param searchQueryRequest
     * @return
     */
    @PostMapping("/all")
    public SearchVO searchAll(@RequestBody SearchQueryRequest searchQueryRequest, HttpServletRequest request) {

        int current = searchQueryRequest.getCurrent();
        int pageSize = searchQueryRequest.getPageSize();
        String type = searchQueryRequest.getType();
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getSearchTypeEnum(type);
        ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR, "请求类型为空");
        String searchText = searchQueryRequest.getSearchText();

        //返回数据：
        if (searchTypeEnum == null) {
            CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> {
                return pictureDataSource.doSearch(searchText, current, pageSize);
            });
            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
                return postDataSource.doSearch(searchText, current, pageSize);
            });
            CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
                return userDataSource.doSearch(searchText, current, pageSize);
            });
            //等待所有的线程都执行完，一直会阻塞到这
            CompletableFuture.allOf(pictureTask, userTask, postTask).join();

            //设置返回值：
            try {
                Page<PostVO> postVOPage = postTask.get();
                Page<Picture> picturePage = pictureTask.get();
                Page<UserVO> userVOPage = userTask.get();
                SearchVO searchVO = new SearchVO();
                searchVO.setPostVOList(postVOPage.getRecords());
                searchVO.setUserVOList(userVOPage.getRecords());
                searchVO.setPictureVOList(picturePage.getRecords());
                return searchVO;
            } catch (Exception e) {
                log.error("查询异常");
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }
        } else {
            SearchVO searchVO = new SearchVO();
            //注册器的使用
            DataSource<T> dataSource = dataSourceRegister.getDataSourceByType(type);
            Page<T> page = dataSource.doSearch(searchText, current, pageSize);
            searchVO.setDataList(page.getRecords());
            return searchVO;
        }
    }
}
