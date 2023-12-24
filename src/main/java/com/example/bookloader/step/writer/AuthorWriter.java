package com.example.bookloader.step.writer;

import com.example.bookloader.entity.Author;
import com.example.bookloader.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorWriter implements ItemWriter<Author> {
    private final AuthorRepository elasticsearchRepository;

    @Override
    public void write(Chunk<? extends Author> chunk) {
        List<? extends Author> authors = chunk.getItems();
        log.info("saving authors: {}", authors);
        List<? extends Author> validAuthors = authors.stream()
                .filter(author -> !ObjectUtils.isEmpty(author.getKey()))
                .toList();

        elasticsearchRepository.saveAll(validAuthors);
    }
}
