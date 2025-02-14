package ru.practicum.client.constants;
import java.time.format.DateTimeFormatter;

public class Constants {

    private Constants() {
    }

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
}