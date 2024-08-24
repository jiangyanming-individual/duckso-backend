package com.jiang.duckso.datasource;


import com.jiang.duckso.model.enums.SearchTypeEnum;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


/**
 * 注册器模式的使用
 */
@Component
public class DataSourceRegister {

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private PostDataSource postDataSource;

    Map<String, DataSource<T>> typeDataSourcesMap;

    /**
     * Bean 加载后才会被初始化
     */
    @PostConstruct
    public void doInit() {
        typeDataSourcesMap = new HashMap() {{
            put(SearchTypeEnum.POST.getValue(), postDataSource);
            put(SearchTypeEnum.USER.getValue(), userDataSource);
            put(SearchTypeEnum.PICTURE.getValue(), pictureDataSource);
        }};
    }

    /**
     * 根据类型获取到DataSource
     * @param type
     * @return
     */
    public DataSource<T> getDataSourceByType(String type) {
        return typeDataSourcesMap.get(type);
    }

}
