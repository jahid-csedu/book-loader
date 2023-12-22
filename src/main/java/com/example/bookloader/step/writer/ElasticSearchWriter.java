package com.example.bookloader.step.writer;

import com.example.bookloader.model.Author;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ElasticSearchWriter implements ItemWriter<Author> {
    private final ElasticsearchRepository elasticsearchRepository;
    @Override
    public void write(Chunk<? extends Author> chunk) throws Exception {
        List<? extends Author> authors = chunk.getItems();
        log.info("saving authors: {}", authors);
        elasticsearchRepository.saveAll(authors);
    }
}
