package com.jiang.duckso.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiang.duckso.model.entity.Picture;


/**
 * 图片服务
 */
public interface PictureService {

    Page<Picture> searchPicture(String searchText, long current, long pageSize);
}