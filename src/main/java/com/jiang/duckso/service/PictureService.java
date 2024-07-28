package com.jiang.duckso.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiang.duckso.model.entity.Picture;


/**
 * 图片服务
 */
public interface PictureService {

    /**
     *
     * @param searchText
     * @param current 当前页
     * @param pageSize 页面有多少条数据
     * @return
     */
    Page<Picture> searchPicture(String searchText, long current, long pageSize);
}