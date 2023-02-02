package com.sosim.server.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils {

    private static final ObjectMapper BASIC_MAPPER = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

    public static String toString(Object src) {
        return toString(BASIC_MAPPER, src);
    }

    public static String toString(ObjectMapper objectMapper, Object src) {
        if (src == null) {
            return null;
        }
        if (src instanceof String) {
            return String.valueOf(src);
        }
        if (src instanceof JsonNode) {
            return src.toString();
        }
        try {
            return objectMapper.writeValueAsString(src);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException:{}", e);
            return null;
        }
    }

}
