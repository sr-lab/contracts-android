# Contract Usage in Android Applications

This repository includes a [pipeline](./datasetScripts) to create a dataset of Java and Kotlin applications and an [analysis tool](./contractstudy) to investigate the presence and usage of contracts in those applications.

This is an extension of the tool proposed by J. Dietrich, D. J. Pearce, K. Jezek, and P. Brad in "Contracts in the wild: A study of java programs" (2017).

## Experiment Setup

### Build the dataset

The analysis tool requires a dataset to be evaluated. This repository includes [scripts](./datasetScripts) to build that dataset. Still, you can create a dataset through any other approach, ensuring that the required dataset directory structure is respected.   

### Running through Docker

Inside the repository folder, run the next commands to build the docker image and to run the container mapping the necessary volumes:
```
docker build -t contract-study .

docker run -d -it --name contract-study -v "$(pwd)/contractstudy:/app/contractstudy" -v "$(pwd)/datasetScripts:/app/datasetScripts" contract-study
```

To execute the full experiment for an already existing dataset, run the next command:

```
docker exec contract-study bash -c "cd contractstudy && mvn exec:java -Dexec.mainClass=contractstudy.scripts.RunAllExperiments"

docker cp contract-study:/framework/contractstudy/output ./contractstudy/
```

