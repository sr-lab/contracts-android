#Contracts In the Wild - A Study of Java Programs

## Overview

This repository contains scripts and data to extract semantic annotations and contract checking code from Java and Kotlin programs, and to analyse how contracts are used and evolve.

## Setup

### Running the Docker container

```
docker build -t contracts-study .

docker run -v “$(pwd)/contractstudy:/contractstudy” -v “$(pwd)/datasetscripts:/datasetscripts” contracts-study mvn clean install

mvn exec:java  -Dexec.mainClass=contractstudy.scripts.CollectDatasetStats (or other script)
```
