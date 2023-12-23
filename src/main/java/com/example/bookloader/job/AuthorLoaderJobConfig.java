package com.example.bookloader.job;

import com.example.bookloader.listener.JobCompletionNotificationListener;
import com.example.bookloader.entity.Author;
import com.example.bookloader.step.processor.AuthorProcessor;
import com.example.bookloader.step.writer.AuthorWriter;
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
public class AuthorLoaderJobConfig {
    private final JobRepository jobRepository;

    @Value("classpath:data/authors.txt")
    private Resource authorFeed;

    @Bean
    public Job authorLoaderJob(@Qualifier("authorLoaderStep") Step loaderStep) {
        var name = "Author loader job";
        var builder = new JobBuilder(name, jobRepository);

        return builder.start(loaderStep).listener(new JobCompletionNotificationListener()).build();
    }

    @Bean
    public Step authorLoaderStep(@Qualifier("authorReader") ItemReader<String> reader,
                                 AuthorProcessor processor,
                                 AuthorWriter writer,
                                 PlatformTransactionManager transactionManager) {
        var name = "Insert author data from csv to elasticsearch step";
        var builder = new StepBuilder(name, jobRepository);

        return builder
                .<String, Author>chunk(5, transactionManager)
                .faultTolerant()
                .retryLimit(3)
                .retry(PessimisticLockingFailureException.class)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public FlatFileItemReader<String> authorReader(LineMapper<String> lineMapper) {
        var itemReader = new FlatFileItemReader<String>();
        itemReader.setLineMapper(lineMapper);
        itemReader.setResource(authorFeed);
        return itemReader;
    }
}
