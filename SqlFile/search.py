with open("SqlFile/to_search.sql", "r") as f:
    lines = f.readlines()
    results = ''
    set1 = set()
    for l in lines:
        s = l[48:57]
        if s not in set1:
            results += s + '|'
            print(s)
            set1.add(s)
    with open("search_line.txt", "w") as out:
        out.write(results[:-1])
        print(len(set1))