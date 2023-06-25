# Consumes /usage/contracts/*.json.
# Gets the top n programs with higher usage per contract type.

import os
import json
import matplotlib.pyplot as plt
import numpy as np

directory_path = "./results/usage/contracts"
program_version = "1"
top_n_files = 100

def searchFilesWithMostOccurrences(directory, value, top_n, languageExtension):
  file_occurrences = {}

  for filename in os.listdir(directory):
    if filename.endswith(".json"):
      file_path = os.path.join(directory, filename)
      with open(file_path) as file:
        data = json.load(file)

      count = sum(1 for item in data if item.get("artefact_type") == value and item.get("cu").endswith(languageExtension) and item.get("version").endswith(program_version))
      file_occurrences[file_path] = count

  sorted_files = sorted(file_occurrences.items(), key=lambda x: x[1], reverse=True)
  top_files = sorted_files[:top_n]
  return top_files

def getStatsByLanguage(languageName, languageExtension):
  print(f"\n\n\n ///// {languageName} LANGUAGE ///// \n\n\n")

  preconditions_results = searchFilesWithMostOccurrences(directory_path, "METHOD_PARAMETER", top_n_files, languageExtension)
  postconditions_results = searchFilesWithMostOccurrences(directory_path, "METHOD", top_n_files, languageExtension)
  invariants_results = searchFilesWithMostOccurrences(directory_path, "CLASS", top_n_files, languageExtension)

  print("\n/// PRE-CONDITIONS ///")
  for file_path, count in preconditions_results:
    print(f"File: {file_path} | Occurrences: {count}")

  print("\n/// POST-CONDITIONS ///")
  for file_path, count in postconditions_results:
    print(f"File: {file_path} | Occurrences: {count}")

  print("\n/// CLASS INVARIANTS ///")
  for file_path, count in invariants_results:
    print(f"File: {file_path} | Occurrences: {count}")

  preconditons_paths = [file_path for file_path, _ in preconditions_results]
  preconditons_counts = [count for _, count in preconditions_results]
  postconditions_paths = [file_path for file_path, _ in postconditions_results]
  postconditions_counts = [count for _, count in postconditions_results]
  invariants_paths = [file_path for file_path, _ in invariants_results]
  invariants_counts = [count for _, count in invariants_results]
  all_results = [preconditons_counts, postconditions_counts, invariants_counts]

  # Box Plot

  plt.boxplot(all_results, showfliers=False)
  result_labels = ['Pre-conditions', 'Post-conditions', 'Class Invariants']
  plt.xticks(range(1, len(result_labels) + 1), result_labels)
  plt.title(languageName)
  plt.xlabel('')
  plt.ylabel('Frequency')
  plt.figure()

  # Scatter Plot

  plt.scatter(range(len(preconditons_paths)), preconditons_counts, color='khaki', label='Pre-conditions')
  plt.scatter(range(len(postconditions_paths)), postconditions_counts, color='navajowhite', label='Post-conditions')
  plt.scatter(range(len(invariants_paths)), invariants_counts, color='lightsteelblue', label='Class Invariants')

  # Set x-axis labels and ticks
  x_ticks = range(max(len(preconditons_paths), len(postconditions_paths), len(invariants_paths)))

  plt.xticks(x_ticks, [], rotation=45)
  plt.xlabel('')
  plt.ylabel('Frequency')
  plt.title(languageName)
  plt.legend()
  plt.figure()

getStatsByLanguage("Java", ".java")
getStatsByLanguage("Kotlin", ".kt")

plt.tight_layout()
plt.show()
