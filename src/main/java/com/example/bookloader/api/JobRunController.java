package com.example.bookloader.api;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loader")
public class JobRunController {
    private final Job authorLoaderJob;
    private final JobLauncher jobLauncher;

    public JobRunController(@Qualifier("authorLoaderJob") Job authorLoaderJob, JobLauncher jobLauncher) {
        this.authorLoaderJob = authorLoaderJob;
        this.jobLauncher = jobLauncher;
    }

    @PostMapping("/authors")
    public ResponseEntity<String> loadJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters params = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();

        jobLauncher.run(authorLoaderJob, params);

        return ResponseEntity.ok("Job started");
    }
}
