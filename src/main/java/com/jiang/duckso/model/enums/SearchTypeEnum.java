package com.jiang.duckso.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum SearchTypeEnum {

    USER("用户", "user"),
    POST("帖子", "post"),
    PICTURE("图片", "picture");

    private String text;


    private String value;

    SearchTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 返回valueList
     * @return
     */
    public static List<String> getValues(){
        //返回valueList:
        return Arrays.stream(values()).map(item->item.value).collect(Collectors.toList());
    }

    /**
     * 遍历枚举类：
     * @param value
     * @return
     */
    public static SearchTypeEnum getSearchTypeEnum(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        //遍历values:
        for (SearchTypeEnum anEnum : SearchTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }


    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }
}
