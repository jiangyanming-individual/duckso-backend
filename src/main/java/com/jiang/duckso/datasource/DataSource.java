package com.jiang.duckso.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 数据源接口，数据源适配器接口
 * @param <T>
 */
public interface DataSource<T> {

    Page<T> doSearch(String searchText, int current, int pageSize);
}
