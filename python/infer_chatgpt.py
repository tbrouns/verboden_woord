# Databricks notebook source
# !pip install openai

# COMMAND ----------

import configparser
import csv
import glob
import numpy as np
import os
import pandas as pd
import openai
import time
from tqdm import tqdm

from utils import parse_txt, convert_to_dict, convert_dict_to_txt

n_cards = 20

data_dir = "./data/"
os.makedirs(data_dir, exist_ok=True)

config_path = "config.ini"
cfg = configparser.ConfigParser()
cfg.read(config_path)
openai.api_key = cfg["openai"]["api_key"]

category_dict = {}
with open("categories.csv", "r") as f:
    reader = csv.reader(f, delimiter=';')
    for line in reader:
        n_samples_for_category, category = line
        category_dict[category] = int(n_samples_for_category)
categories = list(category_dict.keys())
n_categories = len(categories)

for category_index in tqdm(range(n_categories)):

    category = categories[category_index]
    n_samples_for_category = category_dict[category]
    txt_path = os.path.join(data_dir, f"{category}.txt")

    n_samples_old = -n_cards

    print(f"\nProcessing '{category}'...")

    # Get existing words
    if os.path.isfile(txt_path):
        data_dict_existing = parse_txt(txt_path)
    else:
        data_dict_existing = None

    while True:

        if data_dict_existing is not None:
            taboo_words = list(data_dict_existing.keys())
            n_samples = len(taboo_words)
            taboo_words = ", ".join(taboo_words)
        else:
            data_dict_existing = {}
            n_samples = 0
            taboo_words = None

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

        n_samples_old = n_samples

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

        if taboo_words is not None:
            content += (
                "\n\nGenereer geen van de volgende woorden om te raden: "
                f"{taboo_words}"
            )

        response = None
        while response is None:
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

        if response is not None:
            output = response.choices[0].message.content
            lines = output.split("\n")
            data_dict_existing = data_dict_existing | convert_to_dict(lines)

    # Save to file
    lines = convert_dict_to_txt(data_dict_existing)
    with open(txt_path, "w") as f:
        for line in lines:
            line = line.strip()
            f.write(line)
            f.write("\n")

# Convert txt files
txt_file_list = glob.glob(os.path.join(data_dir, "*.txt"))
with open("words.txt", 'w') as f:
    data_dict = {}
    for txt_path in txt_file_list:
        data_dict = data_dict | parse_txt(txt_path)
    lines = convert_dict_to_txt(data_dict)
    for line in lines:
        f.write(line)
        f.write("\n")