# Collecting data from F-Droid

 - F-Droid provides the file [index.xml](https://f-droid.org/repo/index.xml).
   We collected this file on the 22 October 2020. You can use the script
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

 - The file `github_unique_repos.txt` contains the URLs of all the github repositories collected (this file has no duplicate entries). There is a total of 1098 unique repositories.

 - The script `cloneRepos.sh` clones all the repositories listed in the file `github_unique_repos.txt`

## Credits
[Ana Ribeiro](https://github.com/anasofiagribeiro) created the original version
of the script `FDroidStats.py`.
