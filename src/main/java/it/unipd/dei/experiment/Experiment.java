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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Main entry point to the library. Representation of a generic experiment.
 * An experiment has several information associated:
 * <ul>
 *   <li>Category</li>
 *   <li>Date</li>
 *   <li>Name</li>
 *   <li>"Successful" boolean flag</li>
 *   <li>A list of notes, tagged with the date they were created</li>
 *   <li>A list of tags, representing experiment configuration, version of the software used
 *       and so on</li>
 *   <li>A list of tables, containing the actual experimental results.</li>
 * </ul>
 *
 * Several operations can be performed on Experiment objects:
 * <ul>
 *   <li>Add notes and tags: {@link #note(String)}, {@link #tag(String, Object)}</li>
 *   <li>Mark the experiment as failed: {@link #failed()}</li>
 *   <li>Append rows to tables: {@link #append(String, Object...)}
 *                              and {@link #append(String, java.util.Map)}</li>
 *   <li>Save as Ork-mode files: {@link #saveAsOrgFile()}</li>
 *   <li>Save as EDN files: {@link #saveAsEdnFile()}</li>
 *   <li>Save as Json files: {@link #saveAsJsonFile()}</li>
 * </ul>
 */
public class Experiment {

  private static DateTimeFormatter dateFormatter = ISODateTimeFormat.dateTime();

  private String category;
  private DateTime date;
  private String name;
  private boolean successful;
  private List<Note> notes;
  private Map<String, Object> tags;
  private Map<String,Table> tables;

  /**
   * Create a new experiment with the given name and assigned to the given category.
   * @param category the category of the experiment
   * @param name the name of the experiment
   */
  public Experiment(String category, String name) {
    this.category = category;
    this.name = name;
    this.successful = true;
    this.date = DateTime.now();
    notes = new LinkedList<Note>();
    tags = new HashMap<String, Object>();
    tables = new HashMap<String, Table>();
  }

  /**
   * Returns a sha256 hash uniquely identifying this experiment
   */
  protected String sha256() {
    try {
      MessageDigest sha = MessageDigest.getInstance("SHA-256");
      byte[] hash = sha.digest(this.toSimpleString().getBytes("UTF-16"));
      return DatatypeConverter.printHexBinary(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new Error(e);
    } catch (UnsupportedEncodingException e) {
      throw new Error(e);
    }
  }

  /**
   * Marks the experiment as failed.
   * @return a reference to {@code this} for method chaining
   */
  public Experiment failed() {
    this.successful = true;
    return this;
  }

  /**
   * Adds a tag to this experiment.
   * @param name the name of the tag
   * @param value the value of the tag
   * @return a reference to {@code this} for method chaining
   */
  public Experiment tag(String name, Object value) {
    tags.put(name, value);
    return this;
  }

  /**
   * Adds a note to the experiment. A note can be any string. A typical use case is
   * to record an exception.
   * @param message the message of the note
   * @return a reference to {@code this} for method chaining
   */
  public Experiment note(String message) {
    notes.add(new Note(DateTime.now(), message));
    return this;
  }

  /**
   * Append a row to the given table. If the table does not exist, create it.
   * Rows are specified as a varargs array, that is you can specify column names
   * and associated values separated by commas.
   *
   * <pre><code>
   *   experiment.append("tableName",
   *     "column1", value1,
   *     "column3", value3,
   *     "column2", value2);
   * </code></pre>
   *
   * Is equivalent to appending the following row to table {@code "tableName"}
   *
   * <pre><code>
   *   | column1 | column2 | column3 |
   *   |---------+---------+---------|
   *   | value1  | value2  | value3  |
   * </code></pre>
   *
   * @param tableName the name of the table to which add the row.
   * @param rowElements the elements of the row, as a varargs array
   * @return a reference to {@code this} for method chaining
   */
  public Experiment append(String tableName, Object... rowElements) {
    if(!tables.containsKey(tableName)) {
      tables.put(tableName, new Table());
    }
    tables.get(tableName).addRow(rowElements);
    return this;
  }

  /**
   * Like {@link #append(String, Object...)}, with a Map representing the row to
   * be appended instead of a varargs array.
   * @param tableName the name of the table to which add the row.
   * @param row the elements of the row, as Map.
   * @return a reference to {@code this} for method chaining
   */
  public Experiment append(String tableName, Map<String, Object> row) {
    if(!tables.containsKey(tableName)) {
      tables.put(tableName, new Table());
    }
    tables.get(tableName).addRowMap(row);
    return this;
  }

  /**
   * Simple string representation of the experiment.
   * @return a simple string representation.
   */
  public String toSimpleString() {
    StringBuffer sb = new StringBuffer();
    sb.append("==== ").append(name)
      .append(" [").append(category).append("] ")
      .append(" ====\n\n");
    sb.append("Date ").append(dateFormatter.print(date)).append("\n\n");
    sb.append("---- Tags ----\n\n");
    for(Map.Entry<String, Object> t : tags.entrySet()) {
      sb.append("    ").append(t.getKey())
        .append(" : ").append(t.getValue()).append("\n");
    }
    sb.append("\n---- Tables ----\n\n");
    for(Map.Entry<String, Table> t : tables.entrySet()) {
      sb.append("-- ").append(t.getKey()).append(" --\n\n")
       .append(t.getValue().asOrgTable()).append('\n');
    }
    return sb.toString();
  }

  @Override
  public String toString() {
    return "Experiment{category = " + category +
        ", name" + name + ", " + dateFormatter.print(date) + "}";
  }

  protected String getCategory() {
    return category;
  }

  protected String getName() {
    return name;
  }

  protected DateTime getDate() {
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

  private File getOutDir(String directory) {
    File catDir = new File(directory, category);
    File dir = new File(catDir, name);
    if(!dir.exists() && !dir.mkdirs()) {
      throw new RuntimeException("Cannot create " + directory + "directory");
    }
    return dir;
  }

  private File getOutFile(File dir, String extension) {
    String fileName = dateFormatter.print(date) + "-" + sha256() + extension;
    return new File(dir, fileName);
  }

  /**
   * Saves the experiment as a  <a href="http://orgmode.org/">Org-mode</a> file.
   *
   * The file is saved in the current working directory or in the directory specified
   * by the system property {@code experiments.report.dir}.
   *
   * @throws FileNotFoundException
   */
  public void saveAsOrgFile() throws FileNotFoundException {
    this.saveAsOrgFile(System.getProperty("experiments.report.dir", "./reports"));
  }

  /**
   * Saves the experiment as a  <a href="http://orgmode.org/">Org-mode</a> file.
   *
   * @param directory The directory in which to save the org mode file
   * @throws FileNotFoundException
   */
  public void saveAsOrgFile(String directory) throws FileNotFoundException {
    File dir = getOutDir(directory);
    File outFile = getOutFile(dir, ".org");
    PrintWriter out = new PrintWriter(new FileOutputStream(outFile));
    out.write(OrgFileFormatter.format(this));
    out.close();
  }

  /**
   * Saves the experiment as a <a href="https://github.com/edn-format/edn">EDN</a> file.
   *
   * The file is saved in the current working directory or in the directory specified
   * by the system property {@code experiments.report.dir}.
   *
   * @throws FileNotFoundException
   */
  public void saveAsEdnFile() throws FileNotFoundException {
    this.saveAsEdnFile(System.getProperty("experiments.report.dir", "./reports"));
  }

  /**
   * Saves the experiment as a <a href="https://github.com/edn-format/edn">EDN</a> file.
   *
   * @param directory the directory in which to save the EDN file
   * @throws FileNotFoundException
   */
  public void saveAsEdnFile(String directory) throws FileNotFoundException {
    File dir = getOutDir(directory);
    File outFile = getOutFile(dir, ".edn");
    PrintWriter out = new PrintWriter(new FileOutputStream(outFile));
    out.write(EdnFormatter.format(this));
    out.close();
  }

  /**
   * Saves the experiment as a JSON file.
   *
   * The file is saved in the directory specified
   * by the system property {@code experiments.report.dir}.
   *
   * The system property {@code experiment.json.pretty} controls the pretty printing of the output.
   *
   * @throws FileNotFoundException
   */
  public void saveAsJsonFile() throws FileNotFoundException {
    this.saveAsJsonFile(
            System.getProperty("experiments.report.dir", "./reports"),
            Boolean.parseBoolean(System.getProperty("experiment.json.pretty", "false")));
  }

  /**
   * Saves the experiment as a JSON file.
   *
   * The system property {@code experiment.json.pretty} controls the pretty printing of the output.
   *
   * @param directory the directory in which to save the json file
   * @throws FileNotFoundException
   */
  public void saveAsJsonFile(String directory) throws FileNotFoundException {
    this.saveAsJsonFile(directory,
            Boolean.parseBoolean(System.getProperty("experiment.json.pretty", "false")));
  }

  /**
   * Saves the experiment as a JSON file.
   *
   * The file is saved in the directory specified by the system
   * property {@code experiments.report.dir}.
   *
   * @param pretty whether to pretty print the Json file
   * @throws FileNotFoundException
   */
  public void saveAsJsonFile(boolean pretty) throws FileNotFoundException {
    this.saveAsJsonFile(System.getProperty("experiments.report.dir", "./reports"), pretty);
  }

  /**
   * Saves the experiment as a JSON file.
   *
   * @param directory the directory in which to save the json file
   * @param pretty whether to pretty print the Json file
   * @throws FileNotFoundException
   */
  public void saveAsJsonFile(String directory, boolean pretty) throws FileNotFoundException {
    File dir = getOutDir(directory);
    File outFile = getOutFile(dir, ".json");
    PrintWriter out = new PrintWriter(new FileOutputStream(outFile));
    out.write(JsonFormatter.format(this, pretty));
    out.close();
  }

  protected static class Note {
    protected DateTime date;
    protected String message;

    public Note(DateTime date, String message) {
      this.date = date;
      this.message = message;
    }
  }

}
