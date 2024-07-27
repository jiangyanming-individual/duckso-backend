package com.jiang.duckso.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiang.duckso.model.entity.Picture;
import com.jiang.duckso.service.PictureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 图片服务实现：
 */
@Service
@Slf4j
public class PictureServiceImpl implements PictureService {
    /**
     * 搜索图片
     * @param searchText
     * @param current
     * @param pageSize
     * @return
     */
    @Override
    public Page<Picture> searchPicture(String searchText, long current, long pageSize) {
        //todo 进行业务逻辑的操作：
        return null;
    }
}
