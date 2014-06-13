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
in project written in any JVM language with Java interoperability
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

    Date 2014-06-13T21:57:30.113+0200

    ---- Tags ----

        parameter 1 : 123
        another parameter : value

    ---- Tables ----

    -- main-result --

    | parameter 1 | another parameter | first result | second result |
    |-------------+-------------------+--------------+---------------|
    | 123         | value             | -841122125   | 1896525101    |

    -- timing --

    | parameter 1 | another parameter | time | iteration |
    |-------------+-------------------+------+-----------|
    | 123         | value             | 668  | 0         |
    | 123         | value             | 834  | 1         |
    | 123         | value             | 942  | 2         |
    | 123         | value             | 317  | 3         |
    | 123         | value             | 1800 | 4         |

and, more importantly, the following [EDN](https://github.com/edn-format/edn) file

```clojure
;; File name-2014-06-13T21:57:30.113+0200.edn
{:class "experiment-category",
 :name "name",
 :successful true,
 :date "2014-06-13T21:57:30.113+0200",
 :notes [],
 :tags {"another parameter" "value", "parameter 1" 123},
 :tables
 {"timing"
  [{"time" 668, "iteration" 0}
   {"time" 834, "iteration" 1}
   {"time" 942, "iteration" 2}
   {"time" 317, "iteration" 3}
   {"time" 1800, "iteration" 4}],
  "main-result"
  [{"second result" 1896525101, "first result" -841122125}]}}
```

that is suitable to be loaded in a Clojure REPL for further processing,
for instance with [Incanter](http://incanter.org/).
