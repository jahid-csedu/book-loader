package com.example.bookloader.step.processor;

import com.example.bookloader.entity.Author;
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
public class AuthorProcessor implements ItemProcessor<String, Author> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Author process(String line) {
        // Extract JSON object from the line
        log.info("Processing line: {}", line);
        Author.AuthorBuilder authorBuilder = Author.builder();

        try {
            String jsonStr = line.substring(line.indexOf("{"));
            JsonNode jsonNode = objectMapper.readTree(jsonStr);

            // Map JSON properties to Author fields
            List<String> alternateNames = parseAlternateNames(jsonNode);
            authorBuilder.key(parseKey(jsonNode))
                    .name(parseTextField(jsonNode, "name"))
                    .personalName(parseTextField(jsonNode, "personal_name"))
                    .birthDate(parseTextField(jsonNode, "birth_date"))
                    .bio(parseNestedTextField(jsonNode, "bio", "value"))
                    .alternameNames(alternateNames);
        } catch (Exception e) {
            log.error("failed to parse author");
        }

        Author author = authorBuilder.build();
        log.info("Author: {}", author);

        return author;
    }

    private static String parseNestedTextField(JsonNode jsonNode, String parent, String child) {
        return jsonNode.path(parent).path(child).asText();
    }

    private String parseTextField(JsonNode jsonNode, String fieldName) {
        return jsonNode.path(fieldName).asText();
    }

    private static String parseKey(JsonNode jsonNode) {
        String key = jsonNode.path("key").asText();
        return key.replace("/authors/", "");
    }

    private List<String> parseAlternateNames(JsonNode jsonNode) {
        List<String> alternateNames = new ArrayList<>();
        JsonNode alternateNamesNode = jsonNode.path("alternate_names");
        if (alternateNamesNode.isArray()) {
            Iterator<JsonNode> iterator = alternateNamesNode.elements();
            while (iterator.hasNext()) {
                alternateNames.add(iterator.next().asText());
            }
        }

        return alternateNames;
    }
}
