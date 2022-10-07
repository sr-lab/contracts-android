import requests
import os
from dotenv import load_dotenv

load_dotenv("filePaths.env")

URL = "https://f-droid.org/repo/index.xml"
OUTPUT_FILE_PATH = os.getenv('F-DROID-INDEX-FILE')

os.makedirs(os.path.dirname(OUTPUT_FILE_PATH), exist_ok=True)

response = requests.get(URL, stream = True)

with open(OUTPUT_FILE_PATH, "wb") as file:
    for chunk in response.iter_content(chunk_size=1024):
        if chunk:
            file.write(chunk)

print("F-Droid index was downloaded and saved to " + OUTPUT_FILE_PATH)