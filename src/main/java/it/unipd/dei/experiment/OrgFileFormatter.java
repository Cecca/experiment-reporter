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

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Map;

public class OrgFileFormatter {

  public static DateTimeFormatter orgDateFormat = DateTimeFormat.forPattern("[yyyy-MM-dd EEE HH:mm]");

  public static String format(Experiment experiment) {
    StringBuffer sb = new StringBuffer();
    sb.append(headline(experiment));
    if(!experiment.isSuccessful())
      sb.append("  *Failed experiment*\n");
    sb.append(notes(experiment));
    sb.append(tags(experiment));
    sb.append(tables(experiment));
    return sb.toString();
  }

  private static String headline(Experiment experiment) {
    StringBuffer sb = new StringBuffer();
    sb.append("*")
      .append("  ").append(orgDateFormat.print(experiment.getDate()))
      .append("\n");
    return sb.toString();
  }

  private static String notes(Experiment experiment) {
    StringBuffer sb = new StringBuffer();
    for(Experiment.Note note : experiment.getNotes()) {
      sb.append("  - ")
        .append(orgDateFormat.print(note.date)).append("  ")
        .append(note.message).append("\n");
    }
    return sb.toString();
  }

  private static String tags(Experiment experiment) {
    StringBuffer sb = new StringBuffer();
    sb.append("** Tags\n");
    for(Map.Entry<String, Object> t : experiment.getTags().entrySet()) {
      sb.append("   - ").append(t.getKey())
        .append(" : ").append(t.getValue().toString())
        .append("\n");
    }
    return sb.toString();
  }

  private static String tables(Experiment experiment) {
    StringBuffer sb = new StringBuffer();
    sb.append("** Tables\n");
    for(Map.Entry<String, Table> t : experiment.getTables().entrySet()) {
      sb.append("*** ").append(t.getKey()).append("\n");
      sb.append(t.getValue().asOrgTable()).append("\n");
    }
    return sb.toString();
  }

}
