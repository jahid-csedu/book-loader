package com.example.bookloader.job;

import com.example.bookloader.model.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ElasticSearchWriter implements ItemWriter<Person> {
    private final ElasticsearchRepository elasticsearchRepository;
    @Override
    public void write(Chunk<? extends Person> chunk) throws Exception {
        elasticsearchRepository.saveAll(chunk.getItems());
    }
}
