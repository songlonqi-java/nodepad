package com.zy.observable.server.dao;

import com.zy.observable.server.bean.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookDao extends MongoRepository<Book,Integer>{
}
