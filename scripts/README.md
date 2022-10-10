# Collecting data from F-Droid

This collection provides different scripts to manage and clone Android projects.

Currently, projects can be downloaded from [F-Droid](https://f-droid.org) index.

The available [Makefile](./Makefile) makes it easier to automatize some tasks.

## Installation Requirements

### Install Python libraries

The provided scripts are written in Python and some may require additional libraries to be installed. In order to install those libraries, run as a command:

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

To run the complete flow of filtering and cloning projects from the F-Droid index, run as a command:
```
make all-fdroid
```
This command will:

1. Download the F-Droid index xml file.
2. Filter F-Droid projects according to some defined requirements and characteristics.
3. Clone each validated project.

### Java and Kotlin projects support

In the `config.env` file you can specify your interest in analyzing/cloning Java and/or Kotlin projects.
```
JAVA-PROJECTS-ANALYSIS = <boolean>
KOTLIN-PROJECTS-ANALYSIS = <boolean>
```
If those variables are not present in the `config.env` file, scripts are going to consider both languages as accepted.

### Pagination (WIP)

Since the execution of scripts that analyse and/or clone Github projects may take a while, you can paginate the number of projects to analyse/clone each time by specifying an offset and limit variables in `config.env` file such as
```
F-DROID-STATS-REQUEST-OFFSET={insert-number}
F-DROID-STATS-REQUEST-LIMIT={insert-number}
```

If those variables are not present in the `config.env` file, the script will analyse/fetch all projects.

### Output File Paths

Many scripts create output files. In general, the location and names for those files are specified in the [filePaths.env](./filePaths.env) file.



## Available Scripts                                       

| Script |      Description                    |   File Input  |     File Output  |
|------: |-------------------------------------|-------------|-------------|
| [getFDroidIndex.py](./getFDroidIndex.py) |  Downloads F-Droid projects index. | none | [fdroid-index.xml](https://f-droid.org/repo/index.xml) |
| [getFDroidStats.py](./getFDroidStats.py) | Filters F-Droid projects and fetches info for each. This script results can be paginated in order to segment analysis.   |[fdroid-index.xml](https://f-droid.org/repo/index.xml) |   fdroid-stats.csv; repos-registry.txt |
| [cloneRepos.py](./cloneRepos.py) | Clones github repositories. | repos-registry.txt | cloned repos |

## Credits

[Ana Ribeiro](https://github.com/anasofiagribeiro) created the original version
of the script `FDroidStats.py`.
