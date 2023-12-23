package com.example.bookloader.job;

import com.example.bookloader.entity.Book;
import com.example.bookloader.listener.JobCompletionNotificationListener;
import com.example.bookloader.entity.Author;
import com.example.bookloader.step.processor.BookProcessor;
import com.example.bookloader.step.writer.AuthorWriter;
import com.example.bookloader.step.writer.BookWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BookLoaderJobConfig {
    private final JobRepository jobRepository;

    @Value("classpath:data/books.txt")
    private Resource bookFeed;

    @Bean
    public Job bookLoaderJob(@Qualifier("bookLoaderStep") Step bookLoaderStep) {
        var name = "Book loader job";
        var builder = new JobBuilder(name, jobRepository);

        return builder.start(bookLoaderStep).listener(new JobCompletionNotificationListener()).build();
    }

    @Bean
    public Step bookLoaderStep(@Qualifier("bookReader") ItemReader<String> reader,
                               BookProcessor processor,
                               BookWriter writer,
                               PlatformTransactionManager transactionManager) {
        var name = "Insert book data from csv to elasticsearch step";
        var builder = new StepBuilder(name, jobRepository);

        return builder
                .<String, Book>chunk(5, transactionManager)
                .faultTolerant()
                .retryLimit(3)
                .retry(PessimisticLockingFailureException.class)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public FlatFileItemReader<String> bookReader(LineMapper<String> lineMapper) {
        var itemReader = new FlatFileItemReader<String>();
        itemReader.setLineMapper(lineMapper);
        itemReader.setResource(bookFeed);
        return itemReader;
    }
}
