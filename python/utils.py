def convert_to_dict(lines, min_chars=10):
    data_dict = {}
    prev_line = None
    for line in lines:
        line = line.strip()
        prefix = "Te raden"
        split_pattern = "; Verboden woorden: "
        if prefix in line and split_pattern not in line:
            prev_line = line
            process_line = False
        elif prefix not in line and split_pattern in line:
            line = f"{prev_line} {line}"
            process_line = True
        elif len(line) > min_chars:
            process_line = True
        else:
            process_line = False
        if process_line:
            prev_line = None
            if split_pattern in line:
                guess_word, taboo_words = line.split(split_pattern)
                # Get the guess word
                guess_word = guess_word.split(": ")[-1]
                # Get the taboo words
                taboo_words = taboo_words.split(",")
                taboo_words = [w.rstrip('\n').strip() for w in taboo_words]
            else:
                words = line.split(",")
                words = [w.strip() for w in words]
                guess_word = words[0]
                taboo_words = words[1:]
            # Save to dict
            data_dict[guess_word] = taboo_words
    return data_dict


def parse_txt(txt_path):
    lines = []
    with open(txt_path) as f:
        for line in f.readlines():
            lines.append(line)
    data_dict = convert_to_dict(lines)
    return data_dict


def convert_dict_to_txt(data_dict):
    lines = []
    for guess_word, taboo_words in data_dict.items():
        taboo_words_str = ",".join(taboo_words)
        line = f"{guess_word},{taboo_words_str}"
        lines.append(line)
    return lines