def parse_txt(txt_path):
    data_dict = {}
    with open(txt_path) as f:
        for line in f.readlines():
            split_pattern = "; Verboden woorden: "
            if split_pattern in line:
                guess_word, taboo_words = line.split(split_pattern)
                # Get the guess word
                guess_word = guess_word.split(": ")[-1]
                # Get the taboo words
                taboo_words = taboo_words.split(",")
                taboo_words = [w.rstrip('\n') for w in taboo_words]
                # Save to dict
                data_dict[guess_word] = taboo_words
    return data_dict
