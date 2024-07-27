package com.jiang.duckso.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiang.duckso.common.BaseResponse;
import com.jiang.duckso.common.ErrorCode;
import com.jiang.duckso.common.ResultUtils;
import com.jiang.duckso.exception.BusinessException;
import com.jiang.duckso.exception.ThrowUtils;
import com.jiang.duckso.model.dto.picture.PictureQueryRequest;
import com.jiang.duckso.model.entity.Picture;
import com.jiang.duckso.service.PictureService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 图片控制层
 */

@RestController
@RequestMapping("/picture")
public class PictureController {


    @Resource
    private PictureService pictureService;


    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Picture>> listPostVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                        HttpServletRequest request) {

        if (ObjectUtils.isEmpty(pictureQueryRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        int current = pictureQueryRequest.getCurrent();
        int pageSize = pictureQueryRequest.getPageSize();
        //限制爬虫：
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.NO_AUTH_ERROR, "禁止爬虫");
        String searchText = pictureQueryRequest.getSearchText();
        Page<Picture> picturePage = pictureService.searchPicture(searchText, current, pageSize);
        //返回结果：
        return ResultUtils.success(picturePage);
    }


}
