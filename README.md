# Contract Usage in Android Java Applications

This repository contains scripts and data to create a dataset of Java and Kotlin applications, to extract semantic annotations and contract checking code from those Java and Kotlin programs, and to analyse how contracts are used and evolve.

## Setup

### Running the Docker container 

```
docker build -t contracts-study .

docker run -v “$(pwd)/contractstudy:/contractstudy” -v “$(pwd)/datasetscripts:/datasetscripts” contracts-study mvn clean install

mvn exec:java  -Dexec.mainClass=contractstudy.scripts.RunAllExperiments (or other script)
```