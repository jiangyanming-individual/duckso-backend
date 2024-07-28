package com.jiang.duckso.model.dto.search;

import com.jiang.duckso.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 聚合搜索请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SearchQueryRequest extends PageRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String searchText;

    /**
     * 搜索类型
     */
    private String type;

    private static final long serialVersionUID = 1L;
}