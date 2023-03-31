import configparser
import csv
import glob
import os
import openai
import time
from tqdm import tqdm

from utils import parse_txt, convert_to_dict, convert_dict_to_txt


if __name__ == "__main__":

    # Read the config
    config_path = "config.ini"
    assert os.path.isfile(), f"No {config_path} present! Please create it."
    cfg = configparser.ConfigParser()
    cfg.read(config_path)
    openai.api_key = cfg["openai"]["api_key"]

    # Constants
    n_cards = int(cfg["general"]["n_cards"])
    data_dir = cfg["general"]["data_dir"]

    # Create temp directories
    os.makedirs(data_dir, exist_ok=True)

    # Read the categories from the csv
    category_dict = {}
    with open("categories.csv", "r") as f:
        reader = csv.reader(f, delimiter=';')
        for line in reader:
            n_samples_for_category, category = line
            category_dict[category] = int(n_samples_for_category)
    categories = list(category_dict.keys())
    n_categories = len(categories)

    # Generate words in each category using ChatGPT

    for category_index in tqdm(range(n_categories)):

        # Get the category and the number of words to generate
        category = categories[category_index]
        n_samples_for_category = category_dict[category]
        print(f"\nProcessing '{category}'...")

        # Output text path
        txt_path = os.path.join(data_dir, f"{category}.txt")

        # Get existing words
        if os.path.isfile(txt_path):
            data_dict_existing = parse_txt(txt_path)
        else:
            data_dict_existing = None

        # Initialize samples old
        n_samples_old = -n_cards

        # Keep generating words in this category until ...
        # ... we get the minimum number of words, or we cannot generate any more words
        while True:

            if data_dict_existing is not None:
                # Get existing words to not generate again
                taboo_words = list(data_dict_existing.keys())
                n_samples = len(taboo_words)
                taboo_words = ", ".join(taboo_words)
            else:
                data_dict_existing = {}
                n_samples = 0
                taboo_words = None

            # Check if we should stop processing
            if n_samples >= n_samples_for_category or n_samples <= n_samples_old + n_cards // 2:
                print(
                    "\n"
                    f"Stopped processing...\n"
                    f"Samples in current step: {n_samples}\n"
                    f"Samples in previous step: {n_samples_old}\n"
                    f"Total samples: {n_samples_for_category}"
                )
                break
            else:
                print(f"Progress: {n_samples} / {n_samples_for_category}")

            # Create the prompt
            content = (
                f"Genereer {n_cards} speelkaartjes voor het spel 'Taboo' in de categorie: '{category}'.\n\n"
                "Gebruik de volgende voorbeeldlayout voor elk kaartje:\n\n"
                "Te raden: Auto; "
                "Verboden woorden: "
                "Voertuig, "
                "Wielen, "
                "Rijden, "
                "Wagen, "
                "Straat"
            )

            # Add exclusion clause to prompt
            if taboo_words is not None:
                content += (
                    "\n\nGenereer geen van de volgende woorden om te raden: "
                    f"{taboo_words}"
                )

            # Do inference with ChatGPT
            response = None
            while response is None:
                # Keep trying to get a response in case of error
                try:
                    response = openai.ChatCompletion.create(
                        model="gpt-3.5-turbo",
                        messages=[
                            {"role": "user", "content": content},
                        ],
                    )
                except openai.error.APIError as e:
                    print(e)
                    print("Sleeping...")
                    time.sleep(30)

            # If we get a reponse, add to the dictionary
            if response is not None:
                output = response.choices[0].message.content
                lines = output.split("\n")
                data_dict_existing = data_dict_existing | convert_to_dict(lines)

            # Keep track of the number of samples in previous iteration
            n_samples_old = n_samples

        # Convert dictionary to txt and save to disk
        lines = convert_dict_to_txt(data_dict_existing)
        with open(txt_path, "w") as f:
            for line in lines:
                line = line.strip()
                f.write(line)
                f.write("\n")

    # Combine all txt files into single txt
    txt_file_list = glob.glob(os.path.join(data_dir, "*.txt"))
    with open("words.txt", 'w') as f:
        data_dict = {}
        for txt_path in txt_file_list:
            data_dict = data_dict | parse_txt(txt_path)
        lines = convert_dict_to_txt(data_dict)
        for line in lines:
            f.write(line)
            f.write("\n")