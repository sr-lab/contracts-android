# Consumes /usage/contracts/*.json.
# Gets the top n constraints for each artefact (method, method_parameter, class).

import os
import json
from collections import Counter

directory_path = "./results/usage/contracts"
top_n_values = 200
version = "1"

def findTopConstraint(directory, top_n, language_extension, artefact_type):
  value_counts = Counter()

  for filename in os.listdir(directory):
    if filename.endswith(".json"):
      file_path = os.path.join(directory, filename)
      with open(file_path) as file:
        data = json.load(file)

      types = [item.get("type") for item in data
               if item.get("cu").endswith(language_extension)
               and item.get("version") == version
               and item.get("artefact_type") == artefact_type]
      value_counts.update(types)

  top_values = value_counts.most_common(top_n)
  return top_values


def findTopConstraintPerLanguage(language_extension, artefact_type):
  result = findTopConstraint(directory_path, top_n_values, language_extension, artefact_type)
  print(f"\n\n\n Language: {language_extension} / Artefact: {artefact_type}")
  for value, count in result:
    print(f"Value: {value}")
    print(f"Count: {count}")
    print("------------------------")

findTopConstraintPerLanguage(".java", "METHOD_PARAMETER")
findTopConstraintPerLanguage(".java", "METHOD")
findTopConstraintPerLanguage(".java", "CLASS")

findTopConstraintPerLanguage(".kt", "METHOD_PARAMETER")
findTopConstraintPerLanguage(".kt", "METHOD")
findTopConstraintPerLanguage(".kt", "CLASS")