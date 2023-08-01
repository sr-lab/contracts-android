# Removes folders from already preparared dataset.

import os
import zipfile
import shutil

DATASET_PATH = "./output/6-datasets"
FOLDERS_TO_REMOVE = [".git", ".github", "fastlane"]

def remove_git_folders(zip_path):
    temp_dir = "temp_extract"
    with zipfile.ZipFile(zip_path, 'r') as zip_ref:
        zip_ref.extractall(temp_dir)
    
    for root, dirs, files in os.walk(temp_dir):
        for folder in FOLDERS_TO_REMOVE:
            if folder in dirs:
                toRemove = os.path.join(root, folder)
                shutil.rmtree(toRemove)
    
    with zipfile.ZipFile(zip_path, 'w') as zip_ref:
        for foldername, subfolders, filenames in os.walk(temp_dir):
            for filename in filenames:
                file_path = os.path.join(foldername, filename)
                arcname = os.path.relpath(file_path, temp_dir)
                zip_ref.write(file_path, arcname)
    
    shutil.rmtree(temp_dir)

def process_folder(folder_path):
    for root, dirs, files in os.walk(folder_path):
        for filename in files:
            print(filename)
            print("\n")
            if filename.endswith(".zip"):
                zip_path = os.path.join(root, filename)
                remove_git_folders(zip_path)

if __name__ == "__main__": 
    process_folder(DATASET_PATH)