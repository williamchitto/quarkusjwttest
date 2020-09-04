package br.com.psainfo.mimir.core.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import lombok.experimental.UtilityClass;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@UtilityClass
public class GsonUtil {

  @ConfigProperty(name = "GSON_DATE_FORMAT_DESERIALIZE")
  String dateFormat;

  public static final Gson instance() {
    return new GsonBuilder()
        .setDateFormat(dateFormat)
        .registerTypeAdapter(
            LocalDateTime.class,
            (JsonDeserializer<LocalDateTime>)
                (jsonElement, type, jsonDeserializationContext) ->
                    ZonedDateTime.parse(jsonElement.getAsJsonPrimitive().getAsString())
                        .toLocalDateTime())
        .registerTypeAdapter(
            LocalDate.class,
            (JsonDeserializer<LocalDate>)
                (jsonElement, type, jsonDeserializationContext) ->
                    ZonedDateTime.parse(jsonElement.getAsJsonPrimitive().getAsString())
                        .toLocalDate())
        .create();
  }
}
