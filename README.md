experiment-reporter
===================

[![Build Status](https://travis-ci.org/Cecca/experiment-reporter.png)](https://travis-ci.org/Cecca/experiment-reporter)

A simple library to report experimental results.

When doing research work, our programs usually produce lots of logs from which
we have to extract the information we are interested into. This is usually
accomplished with an ad-hoc combination of tools like `grep`, `awk` and the
like or, worse, manually.

While this approach works, it has the downside that it has to be tweaked for
every different experiment and results from different runs have to be manually
combined for further analysis.

This library aims to automate and standardize this time consuming phase of
the experimental process. The application will store the results of the
experiment in an `Experiment` object that will take care of exporting it
in various formats.

The library is written in pure Java with no dependencies, hence it can be used
in projects written in any JVM language with Java interoperability
(Java, Clojure, Scala...).

At a glance
-----------

Before delving into the details of the usage, let's see at a glance some
of the functionality provided. Suppose we have an experiment that performs
several iterations. We want to record the input parameters, the timing of
each iteration and the two numerical results (here generated at random).

```java
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
```

This will generate the following output on the console

    ==== name [experiment-category]  ====

    Date 2014-06-13T22:27:49.655+0200

    ---- Tags ----

        parameter 1 : 123
        another parameter : value

    ---- Tables ----

    -- main-result --

    | parameter 1 | another parameter | first result | second result |
    |-------------+-------------------+--------------+---------------|
    | 123         | value             | -1339574736  | 1418756299    |

    -- timing --

    | parameter 1 | another parameter | time | iteration |
    |-------------+-------------------+------+-----------|
    | 123         | value             | 66   | 0         |
    | 123         | value             | 103  | 1         |
    | 123         | value             | 1517 | 2         |
    | 123         | value             | 39   | 3         |
    | 123         | value             | 278  | 4         |

and, more importantly, the following [EDN](https://github.com/edn-format/edn) file

```clojure
;; File name-2014-06-13T22:27:49.655+0200.edn
{:category "experiment-category",
 :name "name",
 :successful true,
 :date #inst "2014-06-13T22:27:49.000-00:00",
 :notes [],
 :tags {"another parameter" "value", "parameter 1" 123},
 :tables
 {"timing"
  [{"time" 66, "iteration" 0}
   {"time" 103, "iteration" 1}
   {"time" 1517, "iteration" 2}
   {"time" 39, "iteration" 3}
   {"time" 278, "iteration" 4}],
  "main-result"
  [{"second result" 1418756299, "first result" -1339574736}]}}
```

that is suitable to be loaded in a Clojure REPL for further processing,
for instance with [Incanter](http://incanter.org/).
