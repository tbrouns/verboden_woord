def convert_to_dict(lines, min_chars=10):
    """
    Convert the output from LLM or CSV content to dictionary
    :param lines: output lines from LLM following structure from prompt or CSV content
    :param min_chars: minimum numbers of characters in a line
    """
    data_dict = {}
    prev_line = None
    for line in lines:
        line = line.strip()
        # TODO: parametrize the prefix and split_pattern
        prefix = "Te raden"
        split_pattern = "; Verboden woorden: "
        if prefix in line and split_pattern not in line:
            # In case the words for a single card are split between two lines ...
            prev_line = line
            process_line = False
        elif prefix not in line and split_pattern in line:
            # ... we combine with the previous line because the words are split between two lines
            line = f"{prev_line} {line}"
            process_line = True
        elif len(line) > min_chars:
            # Heuristic ...
            process_line = True
        else:
            # The line doesn't have enough characters, so we ignore it
            process_line = False
        if process_line:
            prev_line = None
            if split_pattern in line:  # LLM output
                # Split the line at the provided prompt cue
                guess_word, taboo_words = line.split(split_pattern)
                # Get the guess word
                guess_word = guess_word.split(": ")[-1]
                # Get the taboo words
                taboo_words = taboo_words.split(",")
                taboo_words = [w.rstrip('\n').strip() for w in taboo_words]
            else:  # CSV structure
                # Process the CSV line
                words = line.split(",")
                words = [w.strip() for w in words]
                guess_word = words[0]
                taboo_words = words[1:]
            # Save to dict
            data_dict[guess_word] = taboo_words
    return data_dict


def parse_txt(txt_path):
    """
    Parse the contents of a TXT file
    Can contain output from LLM or CSV content
    :param txt_path: path to TXT file on disk
    :return: dictionary with guess word keys and taboo word values
    """
    # Get the lines from the TXT file
    lines = []
    with open(txt_path) as f:
        for line in f.readlines():
            lines.append(line)
    # Process the lines
    data_dict = convert_to_dict(lines)
    return data_dict


def convert_dict_to_txt(data_dict):
    lines = []
    for guess_word, taboo_words in data_dict.items():
        # Create the txt line
        taboo_words_str = ",".join(taboo_words)
        line = f"{guess_word},{taboo_words_str}"
        # Convert apostrophes to underscore because they cause problems in SQL database
        line = line.split("'", "_")
        lines.append(line)
    return lines
