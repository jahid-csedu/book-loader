package com.example.bookloader.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "authors")
public class Author {
    @Id
    private Long id;

    @Field(type = FieldType.Text, name = "key")
    private String key;

    @Field(type = FieldType.Text, name = "name")
    private String name;

    @Field(type = FieldType.Text, name = "personal_name")
    private String personalName;

    @Field(type = FieldType.Text, name = "birth_date")
    private String birthDate;

    @Field(type = FieldType.Text, name = "alternate_names")
    private List<String> alternameNames;

    @Field(type = FieldType.Text, name = "bio")
    private String bio;
}
