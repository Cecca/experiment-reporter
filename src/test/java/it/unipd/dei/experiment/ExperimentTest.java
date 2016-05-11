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

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ExperimentTest {

  @Test
  public void testAppendApi() {
    Experiment
      exp1 = new Experiment(),
      exp2 = new Experiment();

    exp1
      .append("table",
        "header1", "value",
        "header2", 2)
      .append("table",
        "header1", "other value",
        "header2", 1);

    Map<String,Object>
      firstRow = new HashMap<String, Object>(),
      secondRow = new HashMap<String, Object>();

    firstRow.put("header1", "value");
    firstRow.put("header2", 2);
    secondRow.put("header1", "other value");
    secondRow.put("header2", 1);

    exp2
      .append("table", firstRow)
      .append("table", secondRow);

    assertEquals(
            exp1.getTables().get("table").getRows(),
            exp2.getTables().get("table").getRows());
  }

}
