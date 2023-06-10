import csv
import statistics

def compute_difference(constraints1, methods1, constraints2, methods2):
    ratio1 = constraints1 / methods1 if methods1 != 0 else 0
    ratio2 = constraints2 / methods2 if methods2 != 0 else 0
    difference = ratio2 - ratio1
    return difference

# Path to the CSV file
csv_file = "./evolution/constraintsAcrossVersions.csv"

# Read the CSV file and compute the differences
differences = []
total_difference = 0
num_differences = 0
total_methods_difference = 0
total_contracts_difference = 0
total_methods_version1 = 0
total_methods_version2 = 0
total_contracts_version1 = 0
total_contracts_version2 = 0

number_methods_version1 = []
number_methods_version2 = []
number_contracts_version1 = []
number_contracts_version2 = []


with open(csv_file, "r") as file:
    csv_reader = csv.reader(file)
    next(csv_reader)  # Skip the header row

    for row in csv_reader:
        if len(row) >= 7:
            program = row[0]
            version1 = int(row[1])
            methods1 = int(row[2])
            constraints1 = int(row[3])
            version2 = int(row[4])
            methods2 = int(row[5])
            constraints2 = int(row[6])

            difference_methods = methods2 - methods1
            difference_contracts = constraints2 - constraints1

            difference = compute_difference(constraints1, methods1, constraints2, methods2)
            differences.append((program, version1, version2, methods1, constraints1, methods2, constraints2, difference))

            total_difference += difference
            total_methods_difference += (methods2 - methods1)
            total_contracts_difference += (constraints2 - constraints1)
            num_differences += 1

            total_methods_version1 += methods1
            total_methods_version2 += methods2
            total_contracts_version1 += constraints1
            total_contracts_version2 += constraints2
            number_methods_version1.append(methods1)
            number_methods_version2.append(methods2)
            number_contracts_version1.append(constraints1)
            number_contracts_version2.append(constraints2)

# Print the differences
for program, version1, version2, methods1, constraints1, methods2, constraints2, difference in differences:
    print(f"Program: {program}")
    print(f"Version {version1}: {constraints1}/{methods1} (contracts/methods)")
    print(f"Version {version2}: {constraints2}/{methods2} (contracts/methods)")
    print(f"Evolution in Constracts/Methods Ratio: {difference}\n")



print(f"======== MEDIAN ========")
if num_differences > 0:
    sorted_number_methods_version1 = sorted(number_methods_version1)
    median_number_methods_version1 = statistics.median(sorted_number_methods_version1)
    print(f"Median of methods count in version 1: {median_number_methods_version1}")
    sorted_number_methods_version2 = sorted(number_methods_version2)
    median_number_methods_version2 = statistics.median(sorted_number_methods_version2)
    print(f"Median of methods count in version 2: {median_number_methods_version2}")
    sorted_number_contracts_version1 = sorted(number_contracts_version1)
    median_number_contracts_version1 = statistics.median(sorted_number_contracts_version1)
    print(f"Median of contracts count in version 1: {median_number_contracts_version1}")
    sorted_number_contracts_version2 = sorted(number_contracts_version2)
    median_number_contracts_version2 = statistics.median(sorted_number_contracts_version2)
    print(f"Median of contracts count in version 2: {median_number_contracts_version2}")
else:
    print("No differences found.")



print(f"======== AVERAGES ========")
if num_differences > 0:
    average_methods_version1 = total_methods_version1 / num_differences
    print(f"Average of methods per program in version 1: {average_methods_version1}")
    average_methods_version2 = total_methods_version2 / num_differences
    print(f"Average of methods per program in version 2: {average_methods_version2}")
    average_contracts_version1 = total_contracts_version1 / num_differences
    print(f"Average of contracts per program in version 1: {average_contracts_version1}")
    average_contracts_version2 = total_contracts_version2 / num_differences
    print(f"Average of contracts per program in version 2: {average_contracts_version2}")
else:
    print("No differences found.")



print(f"======== EVOLUTION ========")
if num_differences > 0:
    average_difference = total_difference / num_differences
    average_methods_difference = total_methods_difference / num_differences
    average_contracts_difference = total_contracts_difference / num_differences
    print(f"Average Evolution in Constracts/Methods Ratio per program: {average_difference}")
    print(f"Average increase in Methods number per program in v1 to v2: {average_methods_difference}")
    print(f"Average increase in Contracts number per program in v1 to v2: {average_contracts_difference}")
else:
    print("No differences found.")