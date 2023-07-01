# Merges rows in 3-projects-stats.csv and constraintsAcrossVersions.csv by program name.
# Then, provides some statistics and plots between GitHub metrics and number of contracts in last version.

import csv
import pandas as pd
import matplotlib.pyplot as plt

datasetStatsFile = "./base/3-projects-stats.csv"
constraintsFile = "./results/evolution/constraintsAcrossVersions.csv"

# Merge CSV files.

def replace_slash(string):
    return string.replace("/", "-")

with open(datasetStatsFile, 'r') as file1:
    csv_reader1 = csv.reader(file1)
    next(csv_reader1)  # Skip the header row
    data_dict = {}
    for row in csv_reader1:
        modified_col = replace_slash(row[1].lower())  # Convert to lowercase
        row = row[1:]
        data_dict[modified_col] = row

with open(constraintsFile, 'r') as file2:
    csv_reader2 = csv.reader(file2)
    next(csv_reader2) 
    merged_rows = []
    matched_keys = set()
    for row in csv_reader2:
        program = row[0]
        modified_program = replace_slash(program.lower())  
        if modified_program in data_dict:
            merged_row = data_dict[modified_program] + row
            merged_rows.append(merged_row)
            matched_keys.add(modified_program)

with open('merged.csv', 'w', newline='') as merged_file:
    csv_writer = csv.writer(merged_file)
    csv_writer.writerow(['APPLICATION NAME', 'GITHUB LINK', 'LANGUAGE', 'WATCHERS', 'STARS', 'FORKS', 'CONTRIBUTORS',
                         'DATE OF LAST COMMIT', 'TOTAL MERGED PULL REQUESTS', 'TOTAL CLOSED PULL REQUESTS',
                         '% OF PULL REQUESTS ACCEPTED', 'program', 'version1', 'methods1', 'constraints1',
                         'version2', 'methods2', 'constraints2'])
    csv_writer.writerows(merged_rows)

unmatched_rows = [data_dict[key] for key in data_dict if key not in matched_keys]
print("Unmatched rows:")
for row in unmatched_rows:
    print(row)
    
df = pd.read_csv('./merged.csv')

# Relation between GitHub Metrics and Contracts dispersion plot

column_numbers_dispersion = [3, 4, 5, 6, 10, 17]  
invalid_columns = [col_num for col_num in column_numbers_dispersion if col_num >= len(df.columns)]

filtered_df = df[(df.iloc[:, -1] >= 100) & (df.iloc[:, -1] <= 2000)]
df_filtered = filtered_df.iloc[:, column_numbers_dispersion]
for i in range(len(column_numbers_dispersion) - 1):
    plt.scatter(df_filtered.iloc[:, -1], df_filtered.iloc[:, i], alpha=0.5)
    plt.xlabel(df.columns[column_numbers_dispersion[-1]])
    plt.ylabel(df.columns[column_numbers_dispersion[i]])
    plt.title(f"{df.columns[column_numbers_dispersion[i]]} vs. {df.columns[column_numbers_dispersion[-1]]}")
    plt.figure()


# GitHub Metrics Box plot

column_numbers_box = [3, 4, 5, 6] 
box_labels = ["GitHub Watchers", "GitHub Stars", "GitHub Forks", "GitHub Contributors"]
x_labels = ["watchers", "stars", "forks", "contributors"]    
  
index = 0
for i in column_numbers_box:
    column_number = i
    column_data = df.iloc[:, column_number]
    plt.boxplot(column_data, showfliers=False)
    plt.title(box_labels[index])
    plt.xticks([], [], rotation=45)
    plt.figure()
    print(f"Max outlier in column {df.columns[column_number]}: {df.iloc[:, column_number].max()}")  
    index = index + 1

plt.show()