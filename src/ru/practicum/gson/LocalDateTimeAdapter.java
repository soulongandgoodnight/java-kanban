package ru.practicum.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss.SSSSSS");

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDate) throws IOException {
        if (localDate == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(localDate.format(dtf));
        }
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        var str = jsonReader.nextString();
        if (toString().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(str, dtf);
    }
}