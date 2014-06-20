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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Experiment {

  private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

  private String category;
  private Date date;
  private String name;
  private boolean successful;
  private List<Note> notes;
  private Map<String, Object> tags;
  private Map<String,Table> tables;

  public Experiment(String category, String name) {
    this.category = category;
    this.name = name;
    this.successful = true;
    this.date = new Date();
    notes = new LinkedList<Note>();
    tags = new HashMap<String, Object>();
    tables = new HashMap<String, Table>();
  }

  public Experiment failed() {
    this.successful = true;
    return this;
  }

  public Experiment tag(String name, Object value) {
    tags.put(name, value);
    return this;
  }

  public Experiment note(String message) {
    notes.add(new Note(new Date(), message));
    return this;
  }

  public Experiment append(String tableName, Object... rowElements) {
    if(!tables.containsKey(tableName)) {
      tables.put(tableName, new Table());
    }
    tables.get(tableName).addRow(rowElements);
    return this;
  }

  public Experiment append(String tableName, Map<String, Object> row) {
    if(!tables.containsKey(tableName)) {
      tables.put(tableName, new Table());
    }
    tables.get(tableName).addRowMap(row);
    return this;
  }

  public String toSimpleString() {
    StringBuffer sb = new StringBuffer();
    sb.append("==== ").append(name)
      .append(" [").append(category).append("] ")
      .append(" ====\n\n");
    sb.append("Date ").append(dateFormat.format(date)).append("\n\n");
    sb.append("---- Tags ----\n\n");
    for(Map.Entry<String, Object> t : tags.entrySet()) {
      sb.append("    ").append(t.getKey())
        .append(" : ").append(t.getValue()).append("\n");
    }
    sb.append("\n---- Tables ----\n\n");
    for(Map.Entry<String, Table> t : tables.entrySet()) {
      sb.append("-- ").append(t.getKey()).append(" --\n\n")
       .append(t.getValue().asOrgTable(tags)).append('\n');
    }
    return sb.toString();
  }

  @Override
  public String toString() {
    return "Experiment{category = " + category +
        ", name" + name + ", " + dateFormat.format(date) + "}";
  }

  protected DateFormat getDateFormat() {
    return dateFormat;
  }

  protected String getCategory() {
    return category;
  }

  protected String getName() {
    return name;
  }

  protected Date getDate() {
    return date;
  }

  protected Map<String, Object> getTags() {
    return tags;
  }

  protected Map<String, Table> getTables() {
    return tables;
  }

  public List<Note> getNotes() {
    return notes;
  }

  public boolean isSuccessful() {
    return successful;
  }

  public void saveAsOrgFile() throws FileNotFoundException {
    this.saveAsOrgFile(System.getProperty("experiments.report.dir", "."));
  }

  public void saveAsOrgFile(String directory) throws FileNotFoundException {
    File dir = new File(directory);
    if(!dir.exists() && !dir.mkdir()) {
      throw new RuntimeException("Cannot create " + directory + "directory");
    }
    String fileName = name + "-" + dateFormat.format(date) + ".org";
    File outFile = new File(dir, fileName.replace(" ", "_"));
    PrintWriter out = new PrintWriter(new FileOutputStream(outFile));
    out.write(OrgFileFormatter.format(this));
    out.close();
  }

  public void saveAsEdnFile() throws FileNotFoundException {
    this.saveAsEdnFile(System.getProperty("experiments.report.dir", "."));
  }

  public void saveAsEdnFile(String directory) throws FileNotFoundException {
    File dir = new File(directory);
    if(!dir.exists() && !dir.mkdir()) {
      throw new RuntimeException("Cannot create " + directory + "directory");
    }
    String fileName = name + "-" + dateFormat.format(date) + ".edn";
    File outFile = new File(dir, fileName.replace(" ", "_"));
    PrintWriter out = new PrintWriter(new FileOutputStream(outFile));
    out.write(EdnFormatter.format(this));
    out.close();
  }

  protected static class Note {
    protected Date date;
    protected String message;

    public Note(Date date, String message) {
      this.date = date;
      this.message = message;
    }
  }


  public static void main(String[] args) throws FileNotFoundException {
    Experiment exp = new Experiment("matrix-multiplication", "Test");

    exp.note("This is a test experiment");

    exp.tag("replication", 8)
      .tag("localMemory", 2)
      .tag("dimension", 16);

    exp.append("rounds",
      "round", 0,
      "time", 119823)
      .append("rounds",
        "round", 1,
        "time", 123876);

    exp.note("You can add notes at any time");

    exp.append("radius",
      "radius", 2,
      "count", 10)
      .append("radius",
        "radius", 3,
        "count", 67);

    exp.saveAsEdnFile();

  }
}
