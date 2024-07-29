package com.jiang.duckso.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiang.duckso.common.BaseResponse;
import com.jiang.duckso.common.ErrorCode;
import com.jiang.duckso.common.ResultUtils;
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
import com.jiang.duckso.service.PictureService;
import com.jiang.duckso.service.PostService;
import com.jiang.duckso.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;


/**
 * 聚合接口，实现使用多线程异步的方法
 */

@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Resource
    private UserService userService;

    @Resource
    private PostService postService;

    @Resource
    private PictureService pictureService;

    /**
     * 搜索所有数据源
     *
     * @param searchQueryRequest
     * @return
     */
    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchQueryRequest searchQueryRequest, HttpServletRequest request) {

        int current = searchQueryRequest.getCurrent();
        int pageSize = searchQueryRequest.getPageSize();
        String type = searchQueryRequest.getType();
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getSearchTypeEnum(type);
        ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR, "请求类型为空");
        String searchText = searchQueryRequest.getSearchText();

        //枚举类为空，请求全部：
        //返回数据：
        SearchVO searchVO = new SearchVO();
        if (searchTypeEnum == null){
            CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> {
                return pictureService.searchPicture(searchText, current, pageSize);
            });

            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
                PostQueryRequest postQueryRequest = new PostQueryRequest();
                postQueryRequest.setSearchText(searchText);
                return postService.listPostVOByPage(postQueryRequest, request);
            });

            CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
                UserQueryRequest userQueryRequest = new UserQueryRequest();
                userQueryRequest.setUserName(searchText);
                Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
                return userVOPage;
            });
            //等待所有的线程都执行完，一直会阻塞到这
            CompletableFuture.allOf(pictureTask, userTask, postTask).join();

            //设置返回值：
            try {
                Page<PostVO> postVOPage = postTask.get();
                Page<Picture> picturePage = pictureTask.get();
                Page<UserVO> userVOPage = userTask.get();
                searchVO.setPostVOList(postVOPage.getRecords());
                searchVO.setUserVOList(userVOPage.getRecords());
                searchVO.setPictureVOList(picturePage.getRecords());
            } catch (Exception e) {
                log.error("查询异常");
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }
        }else {
            //分类搜索：
            switch (searchTypeEnum){
                case POST:
                    PostQueryRequest postQueryRequest = new PostQueryRequest();
                    postQueryRequest.setSearchText(searchText);
                    Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);
                    searchVO.setPostVOList(postVOPage.getRecords());
                    break;
                case USER:
                    UserQueryRequest userQueryRequest = new UserQueryRequest();
                    userQueryRequest.setUserName(searchText);
                    Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
                    searchVO.setUserVOList(userVOPage.getRecords());
                    break;
                case PICTURE:
                    Page<Picture> picturePage = pictureService.searchPicture(searchText, current, pageSize);
                    searchVO.setPictureVOList(picturePage.getRecords());
                    break;
                default:
            }
        }
        return ResultUtils.success(searchVO);
    }
}
