#Contracts In the Wild - A Study of Java Programs

## Overview

This repository contains scripts and data to extract semantic annotations and contract checking code from Java programs, and to analyse how contracts are used and evolve. **Note:** we do not track access to this repository via Google Analytics or similar to protect the integrity of the blind review process.

## Setup

### Downloading the Code


To checkout the latest revision, type:
`hg clone https://bitbucket.org/jensdietrich/contractstudy`

To switch to the latest stable release, type:

`hg checkout contractstudy-1.0`

### Building

The project is [Maven](https://maven.apache.org/)-based. Once Maven has been set up, the project can be build by running `mvn clean install`.  Most IDEs including [intellij](https://www.jetbrains.com/idea/)  have built-in support for  Maven projects and mercurial.

### Data Sets

The input data are projects and their versions extracted from the [Maven repository](https://mvnrepository.com/). We extracted the 200 most popular programs, sanitisied them by removing projects and project versions without Java source code, and added the `openjdk 1.8.0_91` sources. The resulting input data set contains 176 projects with 6,934 versions, the
size of the data set is 4.6 GB. Because of size restrictions of bitbucket repositories, the data set is deployed separately, and can be downloaded from [https://goo.gl/2R28gS](https://goo.gl/2R28gS).

### Folder Structure

The code uses the standard Maven directory structure, with the main classes in `src/main/java`. The data is organised as follows: 

Folder        | Description
------------- | -------------
`data`  		| the (separately downloaded) input data should be here, the structure of this folder is `<project-name>/<project-version>.zip`
`out`  		| several data extraction scripts output intermediate data into this folder, formats used are JSON and CSV 
`out/struct` | JSON-encoded structural information extracted from input data, including subtyping, methods, inner class info
`out/contracts` | JSON-encoded contract information extracted from input data
`results`  	| analysis scripts output into this folder, in most cases the output is latex tables to be used directly in the paper, plus log files with additional data (like suspected contract violations) for manual analysis

The folder locations can be overridden by creating a property file `preferences.properties` and putting it into the root folder of the application. The respective properties keys for the folders are `data`, `output` and `results`. Many extraction scripts use concurrency, the key `threads` can be used to configure the number of threads to use, the default is the number of available CPUs.  

## Usage

The various scripts to extract and process data are implemented as Java classes in the `contractstudy.scripts` package. Each script comsumes and produces certain data, this is described in the comments of the respective scripts. 
This creates certain dependencies, i.e., the scripts have to be run in a certain order. 

To facilitate this task, we have created a master script that orchestrates all other scripts, and runs the entire data extraction and analyis starting with the sanitised set of programs downloaded from Maven. This script is `contractstudy.scripts.RunAllExperiments`.

## Extending and Reusing this Study

The data sets can and should be used for other studies. 

For contract-related studies, there are some easy ways to extend our approach. 

1. adding _extractors_ : The main script that gathers contract information is `contractstudy.scripts.CollectContracts`. This script delegates the actual work to _extractors_ (interface `contractstudy.Extractor`), the extractors used are registered in an array in a static variable. Extractors are usually implemented by performing some analysis of the AST using visitors for each compilation unit, looking for patterns. Implemented extractors are in the `contractstudy.extractors` package. 
2. adding _diff rules_ : diffing of contracts is done when evolution and subclassing (LSP violations) are investigated. The core class is `evolutionstudy.Differ`. Again, this class delegates most of the actual work to _diff rules_ (interface`contractstudy.DiffRule`), the rules used are again registered using an array stored in a static variable. Several diff rules are implemented in the `contractstudy.diffrules` package. The existing rules are pretty simple (yet effective in classifying many diff records). More sophisticated rules could use formal reasoning over expression semantics.