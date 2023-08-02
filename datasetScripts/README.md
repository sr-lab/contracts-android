# Collecting data from F-Droid

This collection of Python scripts allow the creation and preparation of a dataset composed by Java and Kotlin applications.

Currently, only [F-Droid](https://f-droid.org) is provided as an index source for projects. Still, given a list of Github URLs, you can use these scripts to clone and prepare any dataset of Java and Kotlin applications (not limited to Android projects).

The available [Makefile](./Makefile) helps to automate various tasks.

- [Collecting data from F-Droid](#collecting-data-from-f-droid)
  - [Installation Requirements](#installation-requirements)
    - [Install Python libraries](#install-python-libraries)
  - [Usage](#usage)
    - [Github Access Key](#github-access-key)
    - [Java and Kotlin projects support](#java-and-kotlin-projects-support)
    - [Pagination (To be improved)](#pagination-to-be-improved)
    - [Output File Paths](#output-file-paths)
    - [Clean output folder.](#clean-output-folder)
    - [Clone F-Droid projects](#clone-f-droid-projects)
    - [Env files to add](#env-files-to-add)

## Installation Requirements

### Install Python libraries

The provided scripts are written in Python, therefore, to install all required libraries (definied in the [requirements.txt](./requirements.txt) file) run the command:
```
make setup
```

## Usage

### Github Access Key

Some scripts make use of Github's APIs/libraries in order to analyse and clone repositories. This way, you may be required to specify your Github account Access Key in an `.env` file such as 
```
GITHUB-ACCESS-TOKEN={your-access-key}
```

### Java and Kotlin projects support

In the `config.env` file you can specify your interest in analyzing/cloning Java and/or Kotlin projects.
```
JAVA-PROJECTS-ANALYSIS = <boolean>
KOTLIN-PROJECTS-ANALYSIS = <boolean>
```
If those variables are not present in the `config.env` file, scripts are going to consider both languages as accepted.

### Pagination (To be improved)

Since the execution of scripts that analyse and/or clone Github projects may take a while, you can segment by chunks the projects to analyse/clone by using the pagination variables - offset and limit - in `config.env` file such as
```
FILTER-PAGINATION-OFFSET={insert-number}
FILTER-PAGINATION-LIMIT={insert-number}
```
If those variables are not present in the `config.env` file, the script will analyse/fetch all projects.

### Output File Paths

Many scripts create output files. In general, the location and names for those files are specified in the [filePaths.env](./filePaths.env) file.

### Clean output folder.
To clean all the output files and cloned projects produced by the scripts, run the command:
```
make clean-output
```

### Clone F-Droid projects

To run the complete flow of filtering and cloning projects from the F-Droid index, run the command:
```
make get-fdroid-dataset
```

| **Script**               | **Description**                                                     | **Input**                     | **Output**                                    |
|--------------------------|---------------------------------------------------------------------|-------------------------------|-----------------------------------------------|
| getFDroidIndex.py        | Downloads F-Droid index file and save each project's URL.           |                               | 0-fdroid-index.xml; 0-f-droid-projects.txt    |
| removeNonGithubURL.py    | Removes non Github URLs.                                            | 0-f-droid-projects.txt        | 1-github-projects.txt                         |
| removeDuplicatedURL.py   | Removes duplicated URLs.                                            | 1-github-projects.txt         | 2-non-duplicated-projects.txt                 |
| filterProjectsByStats.py | Fetches info and stats for each project and filters them.           | 2-non-duplicated-projects.txt | 3-projects-stats.csv; 3-filtered-projects.txt |
| createVersionsFiles.py   | Augments list with URLs for first and last version of each project. | 3-filtered-projects.txt       | 4-projects-versions.txt                       |
| cloneProjects.py         | Clones all listed repositories.                                     | 4-projects-versions.txt       | 5-projects/*                                  |
| cleanProjects.py        | Removes from each project unwanted files such as tests and assets.   | 5-projects/*                  | 5-projects/*                                   |
| prepareDataset.py        | Zips each cloned repositories and creates final folder structure.   | 5-projects/*                  | 6-dataset/*                                   |

### Env files to add

- ".env"

```
GITHUB-ACCESS-TOKEN="xxx"
```

- ".filePaths.env"

```
F-DROID-INDEX-FILE = "output/0-fdroid-index.xml"
F-DROID-PROJECTS-LIST-FILE = "output/0-f-droid-projects.txt"
GITHUB-PROJECTS-LIST-FILE = "output/1-github-projects.txt"
NON-DUPLICATED-PROJECTS-LIST-FILE = "output/2-non-duplicated-projects.txt"
FILTERED-PROJECTS-LIST-FILE = "output/3-filtered-projects.txt"
PROJECTS-STATS-FILE = "output/3-projects-stats.csv"
PROJECTS-VERSIONS-FILE = "output/4-projects-versions.txt"
CLONED-PROJECTS-FOLDER = "output/5-projects"
DATASET-PROJECTS-FOLDER = "output/6-dataset"
DATASET-PROJECTS-LIST = "output/6-dataset.txt"
```

- "config.env"

```
# Filter
JAVA-PROJECTS-ANALYSIS = True
KOTLIN-PROJECTS-ANALYSIS = True
#FILTER-PAGINATION-OFFSET=0
#FILTER-PAGINATION-LIMIT=50

# Versions file
#VERSIONS-FILE-OFFSET=0
#VERSIONS-FILE-LIMIT=10

# Clone
#CLONE-FILE-OFFSET=0
#CLONE-FILE-LIMIT=10
```