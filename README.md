# Book Loader

A Spring Boot project that leverages Spring Batch to load book and author data into Elasticsearch. The project uses an in-memory H2 database for storing batch-related data and Elasticsearch for persisting book and author data.

## Table of Contents
- [Technologies Used](#technologies-used)
- [Features](#features)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [REST Endpoints](#rest-endpoints)
- [Contributing](#contributing)
- [License](#license)

## Technologies Used

- Spring Boot 3.2
- Java 21
- H2 Database (In-memory)
- Elasticsearch
- Elasticsearch Repository Pattern

## Features

- Efficiently loads book and author data into Elasticsearch using Spring Batch.
- Supports two separate jobs: Book Loader Job and Author Loader Job.
- Uses an in-memory H2 database for storing batch-related information.
- Provides REST endpoints for triggering the batch jobs.

## Getting Started

### Prerequisites

Make sure you have the following installed on your machine:

- [Java Development Kit (JDK)](https://adoptopenjdk.net/)
- [Elasticsearch](https://www.elastic.co/guide/en/elasticsearch/reference/current/install-elasticsearch.html)

### Installation

1. Clone the repository:

    ```bash
    git clone https://github.com/yourusername/spring-batch-elasticsearch-loader.git
    ```

2. Navigate to the project directory:

    ```bash
    cd spring-batch-elasticsearch-loader
    ```

3. Build and run the project:

    ```bash
    ./gradlew bootRun
    ```

## Configuration

- Configure Elasticsearch connection details in `application.properties`.

## Usage

To load book data into Elasticsearch, use the following endpoint:

- `POST` `http://localhost:8080/loader/books`

To load author data into Elasticsearch, use the following endpoint:

- `POST` `http://localhost:8080/loader/authors`

## REST Endpoints

- `POST` `http://localhost:8080/loader/books`: Triggers the Book Loader Job.
- `POST` `http://localhost:8080/loader/authors`: Triggers the Author Loader Job.

## Developer
### Md. Jahid Hasan
