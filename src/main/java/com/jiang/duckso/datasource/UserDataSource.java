package com.jiang.duckso.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiang.duckso.model.dto.user.UserQueryRequest;
import com.jiang.duckso.model.vo.UserVO;
import com.jiang.duckso.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserDataSource implements DataSource{


    @Resource
    UserService userService;

    @Override
    public Page<UserVO> doSearch(String searchText, int current, int pageSize) {
        UserQueryRequest userQueryRequest = new UserQueryRequest();
        userQueryRequest.setUserName(searchText);
        userQueryRequest.setCurrent(current);
        userQueryRequest.setPageSize(pageSize);
        return userService.listUserVOByPage(userQueryRequest);
    }
}
