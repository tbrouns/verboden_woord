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

from utils import parse_txt

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
        category_dict[category] = n_samples_for_category
categories = list(category_dict.keys())
n_categories = len(categories)

for category_index in tqdm(range(n_categories)):

    category = categories[category_index]
    n_samples_for_category = category_dict[category]
    txt_path = os.path.join(data_dir, f"{category}.txt")
    n_samples = 0

    while True:

        # Get existing words
        if os.path.isfile(txt_path):
            data_dict = parse_txt(txt_path)
            taboo_words = list(data_dict.keys())
            n_samples = len(taboo_words)
            taboo_words = ", ".join(taboo_words)
        else:
            taboo_words = None

        if n_samples >= n_samples_for_category:
            break

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
            with open(txt_path, "a") as f:
                f.write(output.strip())
