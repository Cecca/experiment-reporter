/*
 * Copyright 2014 Matteo Ceccarello
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.unipd.dei.experiment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class EdnFormatter {

  private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

  public static String format(Experiment experiment) {
    StringBuffer sb = new StringBuffer();
    sb.append("{");
    sb.append(format("experiment-class", "")).append(" ")
      .append(format(experiment.getExperimentClass(), "")).append("\n");

    sb.append(format("name", " ")).append(" ")
      .append(format(experiment.getName(), "")).append("\n");

    sb.append(format("date", " ")).append(" ")
      .append(format(experiment.getDate(), "")).append("\n");

    sb.append(format("notes", " ")).append(" ")
      .append(format(experiment.getNotes(), "         ")).append("\n");

    sb.append(format("tables", " ")).append(" ")
      .append(format(experiment.getTables(), "          "));

    sb.append("}");
    return sb.toString();
  }

  public static String format(Object o, String indent) {
    if(o instanceof String)
      return format((String) o, indent);
    if(o instanceof Number)
      return format((Number) o, indent);
    if(o instanceof Table)
      return format((Table) o, indent);
    if(o instanceof Map)
      return format((Map) o, indent);
    if(o instanceof Date)
      return format((Date) o, indent);
    if(o instanceof Experiment.Note)
      return format((Experiment.Note) o, indent);
    if(o instanceof Collection)
      return format((Collection) o, indent);

    return format(o.toString(), indent);
  }

  public static String format(Collection<Object> coll, String indent) {
    StringBuffer sb = new StringBuffer();
    sb.append("[");

    int n = coll.size();
    int i = 0;
    for(Object obj : coll) {
      sb.append(format(obj, ""));
      if(i++ < n-1) {
        sb.append("\n ").append(indent);
      }
    }
    sb.append("]");

    return sb.toString();
  }

  public static String format(Date date, String indent) {
    return indent + dateFormat.format(date);
  }

  public static String format(Experiment.Note note, String indent) {
    return indent + "[" + format(note.date, "") + " " + format(note.message, "") + "]";
  }

  public static String format(String s, String indent) {
    return indent + "\"" + s + "\"";
  }

  public static String format(Number i, String indent) {
    return indent + i.toString();
  }

  public static String format(Map<String, Object> map, String indent) {
    StringBuffer sb = new StringBuffer();
    sb.append("{");
    int numEntries = map.size();
    int i = 0;
    for(Map.Entry<String, Object> e : map.entrySet()) {
      sb.append(format(e.getKey(), "")).append(" ").append(format(e.getValue(), indent));
      if(i++ < numEntries - 1) {
        sb.append(" ");
      }
    }
    sb.append("}");
    return sb.toString();
  }

  public static String format(Table table, String indent) {
    StringBuffer sb = new StringBuffer();
    sb.append(indent).append("[");
    int numRows = table.getRows().size();
    int i = 0;
    for(Map<String, Object> row : table.getRows()) {
      sb.append(format(row, ""));
      if(i++ < numRows - 1) {
        sb.append("\n ").append(indent);
      }
    }
    sb.append("]");
    return sb.toString();
  }

  public static void main(String[] args) {
    Table t = new Table();

    t.addRow("name", "Matteo", "age", 24).addRow("name", "Martina", "age", 24);

    System.out.println(format(t, ""));
  }

}
