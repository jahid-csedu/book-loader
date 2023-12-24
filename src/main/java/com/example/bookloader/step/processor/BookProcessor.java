package com.example.bookloader.step.processor;

import com.example.bookloader.entity.Book;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@Slf4j
public class BookProcessor implements ItemProcessor<String, Book> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Book process(String line) {
        // Extract JSON object from the line
        log.info("Processing line: {}", line);
        Book.BookBuilder bookBuilder = Book.builder();
        try {
            String jsonStr = line.substring(line.indexOf("{"));
            JsonNode jsonNode = objectMapper.readTree(jsonStr);

            // Map JSON properties to Book fields
            bookBuilder.key(parseKey(jsonNode))
                    .title(parseTextField(jsonNode, "title"))
                    .authors(parseAuthors(jsonNode))
                    .description(parseNestedTextField(jsonNode, "description", "value"))
                    .revision(parseTextField(jsonNode, "revision"))
                    .build();

        } catch (Exception e) {
            log.info("Failed to parse book");
        }

        Book book = bookBuilder.build();
        log.info("Parsed Book: {}", book);
        return book;
    }

    private String parseNestedTextField(JsonNode jsonNode, String parent, String child) {
        return jsonNode.path(parent).path(child).asText();
    }

    private String parseTextField(JsonNode jsonNode, String fieldName) {
        return jsonNode.path(fieldName).asText();
    }

    private String parseKey(JsonNode jsonNode) {
        String key = jsonNode.path("key").asText();
        return key.replace("/works/", "");
    }

    private List<String> parseAuthors(JsonNode jsonNode) {
        List<String> authorIds = new ArrayList<>();
        JsonNode authorNodes = jsonNode.path("authors");
        if (authorNodes.isArray()) {
            Iterator<JsonNode> iterator = authorNodes.elements();
            while (iterator.hasNext()) {
                String authorKey = parseNestedTextField(iterator.next(), "author", "key");
                authorIds.add(authorKey.replace("/authors/", ""));
            }
        }

        return authorIds;
    }
}
