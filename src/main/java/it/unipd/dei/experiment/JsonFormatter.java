package it.unipd.dei.experiment;

import com.google.gson.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.lang.reflect.Type;

public class JsonFormatter {

  private static class DateTimeSerializer implements JsonSerializer<DateTime> {

    private static DateTimeFormatter dateFormatter = ISODateTimeFormat.dateTime();

    @Override
    public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(dateFormatter.print(src));
    }
  }

  private static class TableSerializer implements JsonSerializer<Table> {

    @Override
    public JsonElement serialize(Table src, Type typeOfSrc, JsonSerializationContext context) {
      return context.serialize(src.getRows());
    }
  }

  public static String format(Experiment experiment) {
    return format(experiment, false);
  }

  public static String format(Experiment experiment, boolean pretty) {
    GsonBuilder gsonBuild = new GsonBuilder()
            .registerTypeAdapter(DateTime.class, new DateTimeSerializer())
            .registerTypeAdapter(Table.class, new TableSerializer());

    if(pretty)
      gsonBuild.setPrettyPrinting();

    Gson gson = gsonBuild.create();

    return gson.toJson(experiment);
  }
}
