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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import clojure.lang.*;
import org.junit.Test;

public class EdnFormatterTest {

  public static final Keyword CLASS = Keyword.intern(null, "class");
  public static final Keyword NAME = Keyword.intern(null, "name");
  public static final Keyword DATE = Keyword.intern(null, "date");
  public static final Keyword NOTES = Keyword.intern(null, "notes");
  public static final Keyword TAGS = Keyword.intern(null, "tags");
  public static final Keyword TABLES = Keyword.intern(null, "tables");

  @Test
  public void testEdn1() {
    Experiment exp = new Experiment("exp-class", "exp-name");
    String edn = EdnFormatter.format(exp);
    Object parsed = RT.readString(edn);

    assertThat(parsed, is(instanceOf(IPersistentMap.class)));

    IPersistentMap expMap = (IPersistentMap) parsed;

    assertEquals(expMap.valAt(CLASS), "exp-class");
    assertEquals(expMap.valAt(NAME), "exp-name");
    assertEquals(expMap.valAt(NOTES), PersistentVector.EMPTY);
    assertEquals(expMap.valAt(TAGS), PersistentArrayMap.EMPTY);
    assertEquals(expMap.valAt(TABLES), PersistentArrayMap.EMPTY);
  }

  @Test
  public void testEdnNotes() {
    Experiment exp = new Experiment("exp-class", "exp-name");
    exp.note("This is a test note");

    String edn = EdnFormatter.format(exp);
    Object parsed = RT.readString(edn);

    IPersistentMap expMap = (IPersistentMap) parsed;
    IPersistentVector notes = (IPersistentVector) expMap.valAt(NOTES);

    assertThat(notes.length(), is(1));
    assertThat(notes.nth(0), is(instanceOf(IPersistentVector.class)));
    IPersistentVector note = (IPersistentVector) notes.nth(0);
    assertThat(note.length(), is(2));
    assertEquals(note.nth(1), "This is a test note");
  }

  @Test
  public void testEdnTags() {
    Experiment exp =
      new Experiment("exp-class", "exp-name")
        .tag("tag1", "value 1")
        .tag("tag2", 1234L);

    String edn = EdnFormatter.format(exp);
    Object parsed = RT.readString(edn);

    IPersistentMap expMap = (IPersistentMap) parsed;
    IPersistentMap tags = (IPersistentMap) expMap.valAt(TAGS);

    assertThat(tags.count(), is(2));
    assertEquals(tags.valAt("tag1"), "value 1");
    assertEquals(tags.valAt("tag2"), 1234L);
  }

  @Test
  public void testEdnTables() {
    Experiment exp =
      new Experiment("exp-class", "exp-name")
        .append("table1",
          "head1", "val1",
          "head2", "val2")
        .append("table1",
          "head1", "anotherVal",
          "head2", "anotherVal2")
        .append("table2",
          "head1", "val1",
          "head2", "val2");

    String edn = EdnFormatter.format(exp);
    Object parsed = RT.readString(edn);

    IPersistentMap expMap = (IPersistentMap) parsed;
    IPersistentMap tables = (IPersistentMap) expMap.valAt(TABLES);

    assertThat(tables.count(), is(2));
    assertThat(tables.valAt("table1"), is(instanceOf(IPersistentVector.class)));
    assertThat(tables.valAt("table2"), is(instanceOf(IPersistentVector.class)));

    IPersistentVector table1 = (IPersistentVector) tables.valAt("table1");
    assertThat(table1.count(), is(2));
    assertEquals(((IPersistentMap) table1.nth(0)).valAt("head1"), "val1");
    assertEquals(((IPersistentMap) table1.nth(0)).valAt("head2"), "val2");
    assertEquals(((IPersistentMap) table1.nth(1)).valAt("head1"), "anotherVal");
    assertEquals(((IPersistentMap) table1.nth(1)).valAt("head2"), "anotherVal2");

    IPersistentVector table2 = (IPersistentVector) tables.valAt("table2");
    assertThat(table2.count(), is(1));
    assertEquals(((IPersistentMap) table2.nth(0)).valAt("head1"), "val1");
    assertEquals(((IPersistentMap) table2.nth(0)).valAt("head2"), "val2");
  }

}
