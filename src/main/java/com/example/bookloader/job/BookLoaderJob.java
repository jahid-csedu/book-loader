package com.example.bookloader.job;

import com.example.bookloader.listener.JobCompletionNotificationListener;
import com.example.bookloader.model.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class BookLoaderJob {
    private final JobRepository jobRepository;

    private static final String INSERT_QUERY = """
            insert into person (first_name, last_name, age, is_active)
            values (:firstName,:lastName,:age,:active)
            """;

    @Value("classpath:data/input.csv")
    private Resource inputFeed;

    @Bean
    public Job loadBookToElasticSearchFromCsvFileJob(Step loaderStep) {
        var name = "Book loader job";
        var builder = new JobBuilder(name, jobRepository);

        return builder.start(loaderStep).listener(new JobCompletionNotificationListener()).build();
    }

    @Bean
    public Step bookLoaderStep(ItemReader<Person> reader,
                               ItemWriter<Person> writer,
                               PlatformTransactionManager transactionManager) {
        var name = "Insert data from csv to database step";
        var builder = new StepBuilder(name, jobRepository);

        return builder
                .<Person, Person>chunk(5, transactionManager)
                .faultTolerant()
                .retryLimit(3)
                .retry(PessimisticLockingFailureException.class)
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    public FlatFileItemReader<Person> fileItemReader(LineMapper<Person> lineMapper) {
        var itemReader = new FlatFileItemReader<Person>();
        itemReader.setLineMapper(lineMapper);
        itemReader.setResource(inputFeed);
        return itemReader;
    }

    @Bean
    public DefaultLineMapper<Person> lineMapper(LineTokenizer tokenizer,
                                                FieldSetMapper<Person> mapper) {
        var lineMapper = new DefaultLineMapper<Person>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(mapper);

        return lineMapper;
    }

    @Bean
    public BeanWrapperFieldSetMapper<Person> fieldSetMapper() {
        var fieldSetMapper = new BeanWrapperFieldSetMapper<Person>();
        fieldSetMapper.setTargetType(Person.class);
        return fieldSetMapper;
    }

    @Bean
    public DelimitedLineTokenizer lineTokenizer() {
        var tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setNames("firstName", "lastName", "age", "active");
        return tokenizer;
    }

    @Bean
    public JdbcBatchItemWriter<Person> jdbcBatchItemWriter(DataSource dataSource) {
        var provider = new BeanPropertyItemSqlParameterSourceProvider<Person>();
        var itemWriter = new JdbcBatchItemWriter<Person>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql(INSERT_QUERY);
        itemWriter.setItemSqlParameterSourceProvider(provider);
        return itemWriter;
    }
}
