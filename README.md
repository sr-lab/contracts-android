# Contract Usage in Android Applications

A tool to analyze the presence and usage of contracts in Java and Kotlin applications to understand:
- How and to what extent contracts are used.
- How contracts usage evolves in an application.
- Whether contracts are used safely in the context of program evolution and inheritance.

This repository includes a [pipeline](./datasetScripts) to create a dataset of Java and Kotlin applications and an [analysis tool](./contractstudy) to investigate the contracts in those applications.

## Design-by-Contract

Design-by-Contract is a technique promoted by Bertrand Meyer since the 1980s that purposes the specification of lighweight contracts to create more reliable software. These specifications control the interaction between software components by explicitly specifying the assumptions and responsibilities of each component.

Contracts can either be expressed as preconditions or postconditons, applied to individual methods, or as class invariants, associated to class' properties. A precondition expresses conditions that must be satisfied by the client before invoking the service method; A postcondition expresses properties that are ensured by the supplier to be true before returing the call execution; A class invariant must be satified in the routine's exit if it was satisfied on entry.

### Java and Kotlin support

Neither Java nor Kotlin offers a native standardized approach for contract specification. Still, practitioners can make use of many language features and libraries to make use of Design-by-Contract in their applications.

This analysis tool investigates four different categories of contracts - conditional runtime exceptions, assertions, APIs, annotations, and others - providing insights into how popular each category is. 

Further information can be read in the [analysis tool documentation](./contractstudy/).

## Installation and Usage

The analysis tool supports the investigation of contracts both in Java and Kotlin source code. Therefore, a dataset of applications must first be built to be evaluated by the contracts framework tool.

Further instructions can be read in each component's documentation: [dataset pipeline]("./datasetScripts") and [analysis tool]("./contractstudy").

### Build the dataset

The analysis tool requires a dataset to be evaluated. This repository includes [scripts](./datasetScripts) to build that dataset. Still, you can create a dataset through any other approach, ensuring that the required dataset directory structure is respected.   

### Run the analysis tool

The analysis tool can be executed either through the command line or through IntelliJ. We also provide a Dockerfile to run the experiment inside a container.

#### Run through Docker

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

## Credits and References

This is an extension of the tool proposed by J. Dietrich, D. J. Pearce, K. Jezek, and P. Brada. 2017. Contracts in the Wild: A Study
of Java Programs. In 31st European Conference on Object-Oriented Programming
(ECOOP 2017) (Leibniz International Proceedings in Informatics (LIPIcs), Vol. 74).
Schloss Dagstuhl–Leibniz-Zentrum fuer Informatik, Dagstuhl, Germany, 9:1–
9:29.

Other references:
- B. Meyer. 1992. Applying ‘design by contract’. Computer 25, 10 (1992), 40 – 51.