package com.jiang.duckso.datasource;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiang.duckso.common.ErrorCode;
import com.jiang.duckso.exception.BusinessException;
import com.jiang.duckso.model.entity.Picture;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Service
public class PictureDataSource implements DataSource{
    @Override
    public Page<Picture> doSearch(String searchText, int current, int pageSize) {
        //当前页下标：
        long currentNum = (current - 1) * pageSize;
        String url = String.format("https://www.bing.com/images/search?q=%s&first=%s", searchText, currentNum);
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "爬取网站失败");
        }
        Elements elements = doc.select(".iuscp.isv");
        List<Picture> pictureList = new ArrayList<>();
        for (Element element : elements) {
            String m = element.select(".iusc").get(0).attr("m");
            Picture picture = new Picture();
            //转对象
            Map<String, Object> map = JSONUtil.toBean(m, Map.class);
            String pic_url = (String) map.get("murl");
            picture.setUrl(pic_url);
            //选择器去选择对象：
            String title = element.select(".inflnk").get(0).attr("aria-label");
            picture.setTitle(title);
            pictureList.add(picture);
            if (pictureList.size() > pageSize) {
                break;
            }
        }
        //返回结果：
        Page<Picture> picturePage = new Page<>(current, pageSize);
        picturePage.setRecords(pictureList);
        return picturePage;
    }
}
