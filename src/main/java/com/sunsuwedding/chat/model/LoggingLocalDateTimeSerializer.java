package com.sunsuwedding.chat.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggingLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // 직렬화 전 (Java 내부 값)
        System.out.println("📥 직렬화 전 (Java): " + value);

        // 직렬화 후 (JSON 문자열로 변환될 값)
        String formatted = value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        System.out.println("📤 직렬화 후 (JSON): " + formatted);

        gen.writeString(formatted);
    }
}
