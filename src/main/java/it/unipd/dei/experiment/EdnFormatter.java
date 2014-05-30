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
import java.util.*;

public class EdnFormatter {

  private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

  public static String format(Experiment experiment) {
    return new EdnFormatter().statefulFormat(experiment);
  }

  private String indent = "";

  private void incIndent(int amount) {
    StringBuffer sb = new StringBuffer(indent);
    for(int i=0; i<amount; i++)
      sb.append(' ');
    indent = sb.toString();
  }

  private void decIndent(int amount) {
    StringBuffer sb = new StringBuffer(indent);
    int newSize = sb.length() - amount;
    if(newSize < 0)
      throw new IllegalArgumentException("Cannot decrease indentation to negative numbers");
    indent = sb.substring(0, newSize);
  }

  private String statefulFormat(Experiment experiment) {
    StringBuffer sb = new StringBuffer();
    sb.append("{");
    sb.append(formatClass(experiment)).append("\n");
    incIndent(1);
    sb.append(indent).append(formatName(experiment)).append("\n");
    sb.append(indent).append(formatDate(experiment)).append("\n");
    sb.append(indent).append(formatNotes(experiment)).append("\n");
    sb.append(indent).append(formatTags(experiment)).append("\n");
    sb.append(indent).append(formatTables(experiment));
    sb.append("}");
    return sb.toString();
  }

  private String formatClass(Experiment exp) {
    return fmt("class") + " " + fmt(exp.getExperimentClass());
  }

  private String formatName(Experiment exp) {
    return fmt("name") + " " + fmt(exp.getName());
  }

  private String formatDate(Experiment exp) {
    return fmt("date") + " " + fmt(exp.getDate());
  }

  private String formatNotes(Experiment exp) {
    StringBuffer sb = new StringBuffer();
    sb.append(fmt("notes")).append(" ");
    int indentLen = fmt("notes").length() + 2;
    incIndent(indentLen);
    sb.append(fmt(exp.getNotes()));
    decIndent(indentLen + 1);
    return sb.toString();
  }

  private String formatTags(Experiment exp) {
    StringBuffer sb = new StringBuffer();
    sb.append(fmt("tags")).append(" ");
    sb.append(fmt(exp.getTags()));
    return sb.toString();
  }

  private String formatTables(Experiment exp) {
    StringBuffer sb = new StringBuffer();
    sb.append(fmt("tables")).append(" ");
    int indentLen = fmt("tables").length() + 1;
    incIndent(indentLen);
    int
      i = 0,
      n = exp.getTables().size();

    sb.append("{");
    for(Map.Entry<String, Table> e : exp.getTables().entrySet()) {
      sb.append(fmt(e.getKey())).append("\n").append(indent);
      sb.append(fmt(e.getValue().getRows()));
      if(i++ < n-1) {
        sb.append("\n").append(indent);
      }
    }
    sb.append("}");
    decIndent(indentLen);
    return sb.toString();
  }

  private String fmt(Object o) {
    if(o instanceof String)
      return fmt((String) o);
    if(o instanceof Number)
      return fmt((Number) o);
    if(o instanceof Date)
      return fmt((Date) o);
    if(o instanceof Experiment.Note)
      return fmt((Experiment.Note) o);
    if(o instanceof List)
      return fmt((List) o);
    if(o instanceof Map)
      return fmt((Map) o);

    return fmt(o.toString());
  }

  private String fmt(Number n) {
    return n.toString();
  }

  private String fmt(String s) {
    return "\"" + s + "\"";
  }

  private String fmt(Date d) {
    return "#inst \"" + dateFormat.format(d) + "\"";
  }

  private String fmt(Experiment.Note n) {
    return "[" + fmt(n.date) + " " + fmt(n.message) + "]";
  }

  private String fmt(Map<String, Object> m) {
    StringBuffer sb = new StringBuffer();
    sb.append("{");
    int
      i = 0,
      n = m.size();
    for(Map.Entry<String, Object> e : m.entrySet()) {
      sb.append(fmt(e.getKey())).append(" ").append(fmt(e.getValue()));
      if(i++ < n-1)
        sb.append(" ");
    }
    sb.append("}");
    return sb.toString();
  }

  private String fmt(List<Object> l) {
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    int i = 0;
    int n = l.size();
    for(Object obj : l) {
      sb.append(fmt(obj));
      if(i++ == 1)
        incIndent(1);
      if(i < n)
        sb.append("\n").append(indent);
    }
    sb.append("]");

    return sb.toString();
  }

}
