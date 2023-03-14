package com.dushibao.model;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private Long id;

    private Date addTime;

    private String userName;

    private String password;

    private Date logTime;
}
/*
* CREATE TABLE  student(NAME VARCHAR(10) PRIMARY KEY,
	PASSWORD VARCHAR(10) NOT NULL,
	id VARCHAR(10) NOT NULL);

* */