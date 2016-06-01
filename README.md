experiment-reporter
===================

[![Build Status](https://travis-ci.org/Cecca/experiment-reporter.svg?branch=master)](https://travis-ci.org/Cecca/experiment-reporter)

A simple library to report experimental results.

When doing research work, software usually produces lots of logs from which
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

The library is written Java, hence it can be used in projects written
in any JVM language with Java interoperability (Java, Clojure,
Scala, Groovy...).

At a glance
-----------

Before delving into the details of the usage, let's see at a glance some
of the functionality provided. Suppose we have an experiment that performs
several iterations. We want to record the input parameters, the timing of
each iteration and the two numerical results (here generated at random).

```java
// Setup the experiment
Experiment experiment = new Experiment();

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

// report to console
System.out.println(experiment.toSimpleString());

// report to a JSON file
experiment.saveAsJsonFile();
```

This will generate the following outputs

 - on the console

```
==== 2014-12-02T10:28:18.434+01:00 ====

---- Tags ----

    parameter 1 : 123
    another parameter : value

---- Tables ----

-- main-result --

| first result | second result |
|--------------+---------------|
| -1820064361  | -109620807    |

-- timing --

| time | iteration |
|------+-----------|
| 756  | 0         |
| 151  | 1         |
| 169  | 2         |
| 1189 | 3         |
| 965  | 4         |
```

 - a JSON file

  `File: reports/experiment-category/name/2014-12-02T10:28:18.434+01:00-20B50FC15CD87292576C9DD9BB322F2204DF995BD7192E7FDD177E34C81AED33.json`
```json
{
  "category": "experiment-category",
  "date": "2014-12-02T10:28:18.434+01:00",
  "name": "name",
  "successful": true,
  "notes": [],
  "tags": {
    "parameter 1": 123,
    "another parameter": "value"
  },
  "tables": {
    "main-result": [
      {
        "first result": -1820064361,
        "second result": -109620807
      }
    ],
    "timing": [
      {
        "time": 756,
        "iteration": 0
      },
      {
        "time": 151,
        "iteration": 1
      },
      {
        "time": 169,
        "iteration": 2
      },
      {
        "time": 1189,
        "iteration": 3
      },
      {
        "time": 965,
        "iteration": 4
      }
    ]
  }
}
```

The JSON file is suitable for further processing using
software such as [IPython](http://ipython.org) and
[Incanter](http://incanter.org). 

Configuration with system properties
------------------------------------

Part of the behaviour of the library can be controlled using system
properties:

 - `experiment.report.dir`: configures the directory that will
   contain the reports. Defaults to `./reports`
 - `experiment.report.compress`: whether or not the file should be compressed with gzip.
   Defaults to `true`
 - `experiment.tag.some-tag-name`: Add a tag with the given name and value
   to the experiment at invocation time.
