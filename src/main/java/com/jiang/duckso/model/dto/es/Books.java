package com.jiang.duckso.model.dto.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 测试实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName="books")
public class Books {
    @Id
    private String id;

    /**
     * 字段类型
     */
    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String summary;

    @Field(type = FieldType.Integer)
    private Integer price;
}