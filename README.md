# Contract Usage in Android Applications

This repository includes a pipeline to create a dataset of Java and Kotlin application and a analysis tool to investigate the presence and usage of contracts in those applications.

This is an extension of the tool proposed by J. Dietrich, D. J. Pearce, K. Jezek, and P. Brad in "Contracts in the wild: A study of java programs" (2017).

## Setup

### Running the Docker container 

```
docker build -t contracts-study .

docker run -v “$(pwd)/contractstudy:/contractstudy” -v “$(pwd)/datasetscripts:/datasetscripts” contracts-study mvn clean install

mvn exec:java  -Dexec.mainClass=contractstudy.scripts.RunAllExperiments (or other script)
```