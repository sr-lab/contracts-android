DEBUG=False

import pandas as pd
df = pd.read_excel('FDroidStats_Sorted.xlsx')

unique_repos = []  # we want to use a list to keep the same order!
total = 0
for repo_url in df['GITHUB LINK']:
    total = total + 1
    if repo_url not in unique_repos:
        unique_repos.append(repo_url)

for repo in unique_repos:
    print(repo)

unique = len(unique_repos)

if(DEBUG):
    print(f"Total repos considered: {total}")
    print(f"Unique repos: {unique} ({total-unique} lines removed)")
