package com.example.bookloader.step.writer;

import com.example.bookloader.entity.Book;
import com.example.bookloader.repository.BookRepository;
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
public class BookWriter implements ItemWriter<Book> {
    private final BookRepository elasticsearchRepository;
    @Override
    public void write(Chunk<? extends Book> chunk) throws Exception {
        List<? extends Book> books = chunk.getItems();
        log.info("saving books: {}", books);
        List<? extends Book> validBooks = books.stream()
                .filter(book -> !ObjectUtils.isEmpty(book.getKey()))
                .toList();

        elasticsearchRepository.saveAll(validBooks);
    }
}
