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

import java.io.FileNotFoundException;
import java.util.Random;

public class Example {

  public static void main(String[] args) throws FileNotFoundException, InterruptedException {
    int experimentIterations = 5;

    // Setup the experiment, with category and name
    Experiment experiment = new Experiment("experiment-category", "name");

    // record the input parameters
    experiment
      .tag("parameter 1", 123)
      .tag("another parameter", "value");

    int result1 = 0, result2 = 0;

    for(int i=0; i<experimentIterations; i++) {
      long start = System.currentTimeMillis();
      // do something that modifies result1 and result2
      result1 = new Random().nextInt();
      result2 = new Random().nextInt();
      Thread.sleep(new Random().nextInt(2000));
      long end = System.currentTimeMillis();

      // Record the running time
      experiment.append("timing",
        "iteration", i,
        "time", (end - start));
    }

    // record the results
    experiment.append("main-result",
      "first result", result1,
      "second result", result2);

    // report to console and to an EDN file
    System.out.println(experiment.toSimpleString());
    experiment.saveAsEdnFile();
    experiment.saveAsOrgFile();
    experiment.saveAsJsonFile(true);
  }

}
