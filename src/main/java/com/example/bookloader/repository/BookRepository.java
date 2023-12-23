package com.example.bookloader.repository;

import com.example.bookloader.entity.Author;
import com.example.bookloader.entity.Book;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends ElasticsearchRepository<Book, Long> {
}
