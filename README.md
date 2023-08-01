# Contract Usage in Android Applications

This repository includes a pipeline to create a dataset of Java and Kotlin application and a analysis tool to investigate the presence and usage of contracts in those applications.

This is an extension of the tool proposed by J. Dietrich, D. J. Pearce, K. Jezek, and P. Brad in "Contracts in the wild: A study of java programs" (2017).

## Setup

### Running the Docker container 

Inside the repository folder run the next commands to build the docker image and to run the container mapping the necessary volumes:
```
docker build -t contract-study .

docker run -d -it --name contract-study -v "$(pwd)/contractstudy:/app/contractstudy" -v "$(pwd)/datasetScripts:/app/datasetScripts" contract-study
```

To execute the full experiment for an already existing dataset, run the next command:

```
docker exec contract-study bash -c "cd contractstudy && mvn exec:java -Dexec.mainClass=contractstudy.scripts.RunAllExperiments"

docker cp contract-study:/framework/contractstudy/output ./contractstudy/
```

