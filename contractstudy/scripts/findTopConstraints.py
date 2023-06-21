# Consumes "./results/usage/contracts".
# Finds the top n constraints per language.

import os
import json
from collections import Counter

directory_path = "./results/usage/contracts"
top_n_values = 10

def findTopConstraint(directory, top_n, language_extension):
  value_counts = Counter()

  for filename in os.listdir(directory):
    if filename.endswith(".json"):
      file_path = os.path.join(directory, filename)
      with open(file_path) as file:
        data = json.load(file)

      types = [item.get("type") for item in data  if item.get("cu").endswith(language_extension)]
      value_counts.update(types)

  top_values = value_counts.most_common(top_n)
  return top_values


def findTopConstraintPerLanguage(language_extension):
  result = findTopConstraint(directory_path, top_n_values, language_extension)
  print(f"\n\n\n Language: {language_extension}")
  for value, count in result:
    print(f"Value: {value}")
    print(f"Count: {count}")
    print("------------------------")

findTopConstraintPerLanguage(".java")
findTopConstraintPerLanguage(".kt")