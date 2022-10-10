# Collecting data from F-Droid

This collection provides different scripts to manage and clone Android projects.

Currently, only [F-Droid](https://f-droid.org) is supported as an index source for projects.

The available [Makefile](./Makefile) helsp automating some tasks.

- [Collecting data from F-Droid](#collecting-data-from-f-droid)
  - [Installation Requirements](#installation-requirements)
    - [Install Python libraries](#install-python-libraries)
    - [Github Access Key](#github-access-key)
  - [Usage](#usage)
    - [Clone F-Droid projects](#clone-f-droid-projects)
    - [Java and Kotlin projects support](#java-and-kotlin-projects-support)
    - [Pagination (WIP)](#pagination-wip)
    - [Output File Paths](#output-file-paths)
    - [Clean output folder.](#clean-output-folder)
  - [Available Scripts](#available-scripts)

## Installation Requirements

### Install Python libraries

The provided scripts are written in Python and some may require additional libraries to be installed. In order to install those libraries, run the command:

```
make setup
```
This will install all libraries defined in the [requirements.txt](./requirements.txt) file.

### Github Access Key

Some scripts make use of Github's APIs/libraries in order to analyse and clone repositories. Therefore, you may be required to specify your Github account Access Key in an `.env` file such as 
```
GITHUB-ACCESS-TOKEN={your-access-key}
```

## Usage

### Clone F-Droid projects

To run the complete flow of filtering and cloning projects from the F-Droid index, run the command:
```
make all-fdroid
```
This command will:

1. Download the F-Droid index xml file.
2. Clean list of projects' Github URLs contained in F-Droid index.
3. Filter F-Droid projects according to some defined requirements.
4. Clone each validated project.

### Java and Kotlin projects support

In the `config.env` file you can specify your interest in analyzing/cloning Java and/or Kotlin projects.
```
JAVA-PROJECTS-ANALYSIS = <boolean>
KOTLIN-PROJECTS-ANALYSIS = <boolean>
```
If those variables are not present in the `config.env` file, scripts are going to consider both languages as accepted.

### Pagination (WIP)

Since the execution of scripts that analyse and/or clone Github projects may take a while, you can segment by chunks the projects to analyse/clone by using the pagination variables - offset and limit - in `config.env` file such as
```
F-DROID-STATS-REQUEST-OFFSET={insert-number}
F-DROID-STATS-REQUEST-LIMIT={insert-number}
```
If those variables are not present in the `config.env` file, the script will analyse/fetch all projects.

### Output File Paths

Many scripts create output files. In general, the location and names for those files are specified in the [filePaths.env](./filePaths.env) file.

### Clean output folder.
To clean all the output files produzed by the scripts, run the command:
```
make force-clean
```


## Available Scripts                                       

| Script |      Description                    |   File Input  |     File Output  |
|------: |-------------------------------------|-------------|-------------|
| [getFDroidIndex.py](./getFDroidIndex.py) |  Downloads F-Droid index and outputs its repositories' URLs to a file. | none | [fdroid-index.xml](https://f-droid.org/repo/index.xml); repos-registry.txt |
| [removeDuplicatedURL.py](./removeDuplicatedURL.py) |  Removes all duplicated urls. | repos-registry.txt | repos-registry.txt |
| [removeNonGithubURL.py](./removeNonGithubURL.py) |  Removes all non-Github URLs and standarizes base url. | repos-registry.txt | repos-registry.txt |
| [filterProjectsByStats.py](./filterProjectsByStats.py) | Filters each project according to specified rules and to their Github info and stats. This script results can be paginated in order to segment analysis.   | repos-registry.txt |   repos-stats.csv; repos-registry.txt |
| [cloneRepos.py](./cloneRepos.py) | Clones github repositories. | repos-registry.txt | cloned repos |