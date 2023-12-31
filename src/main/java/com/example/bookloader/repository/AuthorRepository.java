package com.example.bookloader.repository;

import com.example.bookloader.entity.Author;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends ElasticsearchRepository<Author, Long> {
}
