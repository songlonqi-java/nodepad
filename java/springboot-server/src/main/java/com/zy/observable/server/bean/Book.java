package com.zy.observable.server.bean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("book")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    private Long id;
    private String title;
    private Integer page;
}