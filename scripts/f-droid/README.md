# Collecting data from F-Droid

 - F-Droid provides the file [index.xml](https://f-droid.org/repo/index.xml).
   We collected this file on the 22 October 2020 (it contains information about
   3168 projects). You can use the script
   `downloadFDroidIndex.sh` to download the most current version.

 - Projects are filtered using the python script `FDroidStats.py`. To run it,
   we recommend that you create a local virtual environment and install the
   required packages. You can follow these steps:

   ```
   python3 -mvenv local-env
   source local-env/bin/activate
   pip install -r requirements.txt
   ```

   You need to define a variable `ACCESS_TOKEN` in a file called `GithubKeys.py`
   with your Github access token.

   After this, just run the script: `python FDroidStats.py`

   The number of repositories is quite large and since Github only allows 5,000
   requests per hour, execution might take a while.

 - The output files are `FDroidStats.xlsx` and `FDroidStats_Sorted.xlsx`. By default,
   the second file will sort the repositories by:

     - Date of last commit
     - Number of stars
     - Number of watchers
     - Percentage of PRs accepted
     - Total merged pull requests
     
   The files contain 1145 entries (with some duplicates).

 - The script `printUniqueRepos.py` is used to remove duplicates. It was used to generate the file `github_unique_repos.txt`, which contains the URLs of all the github repositories collected without duplicates. There is a total of 1119 unique repositories.

 - The script `createVersionsFile.py` is used to obtain the 'first' and 'last' versions of each repository in `github_unique_repos.txt`. The script can set a limit on the number of projects to process (by default, it processes the top 200 projects). It creates the file `versions_file.txt` with the versions that will be processed.

 - The script `cloneRepos.sh` clones the repositories listed in the file `versions_file.txt`

## Credits
[Ana Ribeiro](https://github.com/anasofiagribeiro) created the original version
of the script `FDroidStats.py`.
