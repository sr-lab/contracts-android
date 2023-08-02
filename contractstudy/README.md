# Static Code Analysis Tool to Investigate Contracts

## Overview

This project investigates the presence and usage of contracts in Java and Kotlin applications. It is divided into three main components: usage, inheritance, and evolution. Although, each script can be run by itself, there are some data requirements that need to be observed.

This tool performs a static analysis of the dataset's source code using the [JavaParser](https://javaparser.org) and [JetBrain's Kotlin compiler](https://github.com/JetBrains/kotlin) libraries.

## Setup and Usage

### Dataset and Results

In the [preferences.properties](preferences.properties) file, you can specify the path of the dataset you want to evaluate and the path to output the results.

### Running The Scripts

The main scripts to conduct the evaluation are located in [/src/main/java/contractstudy/scripts/*](./src/main/java/contractstudy/scripts/) folder.

The script [RunAllExperiments](./src/main/java/contractstudy/scripts/RunAllExperiments.java) invokes all scripts to perform a complete experiment.

You can run all scripts using the terminal through the next command:
```
mvn exec:java  -Dexec.mainClass=contractstudy.scripts.RunAllExperiments (or other scripts)
```

### Additional Scripts

The [./scripts](./scripts) folder contains Python scripts that help to analyze the results.
