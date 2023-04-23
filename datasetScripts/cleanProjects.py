import os
import shutil
from dotenv import load_dotenv

load_dotenv("filePaths.env")

INPUT_FOLDER = os.getenv('CLONED-PROJECTS-FOLDER')

extensions_to_remove = ['.png', '.jpg', '.jpeg', '.svg', '.fnt', '.xml', '.html', '.xcf', '.json', '.apk', '.bin', '.jar']

for project_dirname in os.listdir(INPUT_FOLDER):
    
    project_dir_path = os.path.join(INPUT_FOLDER, project_dirname)
    
    if os.path.isdir(project_dir_path):
        for root, dirs, files in os.walk(project_dir_path):
            
            # Removes unecessary files
            for filename in files:
                if any(filename.lower().endswith(ext) for ext in extensions_to_remove):
                    file_path = os.path.join(root, filename)
                    os.remove(file_path)
            
            # Removes src/test and src/androidTest directory
            if 'src' in dirs:
                test_dir_path = os.path.join(root, 'src', 'test')
                if os.path.exists(test_dir_path):
                    shutil.rmtree(test_dir_path)
                
                test_dir_path = os.path.join(root, 'src', 'androidTest')
                if os.path.exists(test_dir_path):
                    shutil.rmtree(test_dir_path)