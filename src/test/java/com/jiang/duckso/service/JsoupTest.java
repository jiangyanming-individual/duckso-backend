package com.jiang.duckso.service;


import cn.hutool.json.JSONUtil;
import com.jiang.duckso.model.entity.Picture;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
public class JsoupTest {


    @Test
    public void testJsoup() throws IOException {

        int current = 1;
        String url = "https://www.bing.com/images/search?q=小黑子&first=" + current;
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select(".iuscp.isv");

        List<Picture> pictureList=new ArrayList<>();
        for (Element element : elements) {
            String m = element.select(".iusc").get(0).attr("m");
            Picture picture = new Picture();
            //转对象
            Map<String,Object> map = JSONUtil.toBean(m, Map.class);
            String pic_url = (String) map.get("murl");
            picture.setUrl(pic_url);
//            System.out.println(pic_url);
            //选择器去选择对象：
            String title = element.select(".inflnk").get(0).attr("aria-label");
            picture.setTitle(title);
//            System.out.println(title);
        }
    }
}
