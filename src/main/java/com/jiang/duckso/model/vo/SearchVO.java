package com.jiang.duckso.model.vo;

import com.jiang.duckso.model.entity.Picture;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 聚合VO
 */
@Data
public class SearchVO implements Serializable {


    /**
     * 文章列表VO
     */
    private List<PostVO> postVOList;

    /**
     * 用户请求VO
     */
    private List<UserVO> userVOList;

    /**
     * 图片请求VO
     */
    private List<Picture> pictureVOList;


    private List<?> dataList;

    private static final long serialVersionUID = 1L;
}