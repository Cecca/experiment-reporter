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

import java.util.*;

public class Table {

  private Collection<Map<String, Object>> rows;

  private Collection<String> headers;

  public Table() {
    this.rows = new LinkedList<Map<String, Object>>();
    this.headers = null;
  }

  public Table addRow(Object... rowEntries) {
    if(rowEntries.length % 2 != 0) {
      throw new IllegalArgumentException(
        "addRow() should have an even number of arguments");
    }
    HashMap<String, Object> row = new HashMap<String, Object>();
    for(int i = 0; i < rowEntries.length;) {
      row.put(rowEntries[i++].toString(), rowEntries[i++]);
    }
    return this.addRow(row);
  }

  public Table addRow(Map<String, Object> row) {
    if(headers == null) {
      headers = row.keySet();
    } else {
      if(!headers.containsAll(row.keySet())) {
        throw new IllegalArgumentException(
          "Some keys in the given row are not part of the headers of this table");
      }
      if(!row.keySet().containsAll(headers)) {
        throw new IllegalArgumentException(
          "Some keys of the table are missing from the given row");
      }
    }

    rows.add(row);
    return this;
  }

  public String asOrgTable() {
    return this.asOrgTable(headers);
  }

  public String asOrgTable(String... columns) {
    ArrayList<String> cols = new ArrayList<String>(columns.length);
    for(String s : columns) {
      cols.add(s);
    }
    return this.asOrgTable(cols);
  }

  public String asOrgTable(Collection<String> columns) {
    if(!headers.containsAll(columns)) {
      throw new IllegalArgumentException(
        "Some columns are undefined in the table");
    }

    Map<String, Integer> widths = new HashMap<String, Integer>();
    for(String c : columns) {
      widths.put(c, c.length());
    }
    for(Map<String, Object> row : rows) {
      for(String k : columns) {
        widths.put(k,
          Math.max(
            widths.get(k),
            row.get(k).toString().length()));
      }
    }

    StringBuffer sb = new StringBuffer();

    sb.append('|');
    for(String c : columns) {
      sb.append(pad(c, widths.get(c), ' '));
      sb.append('|');
    }
    sb.append('\n');

    sb.append('+');
    for(String c : columns) {
      sb.append(pad("", widths.get(c), '-'));
      sb.append('+');
    }
    sb.append('\n');

    for(Map<String, Object> row : rows) {
      sb.append('|');
      for(String k : columns) {
        sb.append(pad(row.get(k).toString(), widths.get(k), ' '));
        sb.append('|');
      }
      sb.append('\n');
    }

    return sb.toString();
  }

  public static String pad(String s, int width, char ch) {
    StringBuffer sb = new StringBuffer();
    sb.append(ch);
    sb.append(s);
    for(int i=0; i < width - s.length(); i++) {
      sb.append(ch);
    }
    sb.append(ch);
    return sb.toString();
  }

  public static void main(String[] args) {
    Table table = new Table()
      .addRow(
        "name", "matteo",
        "surname", "ceccarello",
        "age", 25)
      .addRow(
        "name", "mario",
        "surname", "rossi",
        "age", 32);

    System.out.println(table.asOrgTable());
    System.out.println(table.asOrgTable("name", "surname", "age"));
  }

}
