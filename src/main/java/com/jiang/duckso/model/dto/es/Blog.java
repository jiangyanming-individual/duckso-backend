package com.jiang.duckso.model.dto.es;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.util.Date;

/**
 * 博客实体类：
 */
@Data
@Document(indexName = "blog", shards = 1, replicas = 1)
public class Blog {
    //此项作为id，不会写到_source里边。
    @Id
    private Long blogId;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Text)
    private String author;
    //博客所属分类。
    @Field(type = FieldType.Keyword)
    private String category;
    //0: 未发布（草稿） 1：已发布 2：已删除
    @Field(type = FieldType.Integer)
    private int status;
    //序列号，用于给外部展示的id
    @Field(type = FieldType.Keyword)
    private String serialNum;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @Field(type= FieldType.Date, format= DateFormat.custom, pattern="yyyy-MM-dd HH:mm:ss.SSS")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @Field(type=FieldType.Date, format=DateFormat.custom, pattern="yyyy-MM-dd HH:mm:ss.SSS")
    private Date updateTime;
}