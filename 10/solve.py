import sys
if len(sys.argv) != 2:
    print('Usage: python solve.py <input-file-name>')
    sys.exit(1)

fname = sys.argv[1]
input = [line.strip() for line in open(fname)]

def is_opening_paren(char):
    return char == '(' or char == '[' or char == '{' or char == '<'

def is_matching_paren(open, close):
    if open == '(' and close != ')':
        return False
    if open == '[' and close != ']':
        return False
    if open == '{' and close != '}':
        return False
    if open == '<' and close != '>':
        return False
    return True

def get_first_illegal_character(line):
    stack = []

    for char in line:
        if is_opening_paren(char):
            stack.append(char)
        else: # closing bracket
            open = stack.pop()

            if not is_matching_paren(open, char):
                return char

    return None

def solve_part1(input):
    point = 0

    for line in input:
        char = get_first_illegal_character(line)
        if char is None: # not found
            continue

        if char == ')':
            point += 3
        elif char == ']':
            point += 57
        elif char == '}':
            point += 1197
        elif char == '>':
            point += 25137

    return point

print('Part 1:', solve_part1(input))

def get_completing_sequences(line):
    stack = []

    for char in line:
        if is_opening_paren(char):
            stack.append(char)
        else: # closing bracket
            open = stack.pop()

            if not is_matching_paren(open, char):
                return None

    seq = [] # buffer
    while len(stack) > 0:
        open = stack.pop()

        if open == '(':
            seq.append(')')
        elif open == '[':
            seq.append(']')
        elif open == '{':
            seq.append('}')
        elif open == '<':
            seq.append('>')
    return ''.join(seq)

def get_score(seq):
    score = 0
    for char in seq:
        score *= 5
        if char == ')':
            score += 1
        elif char == ']':
            score += 2
        elif char == '}':
            score += 3
        elif char == '>':
            score += 4
    return score

def solve_part2(input):
    score = []

    for line in input:
        seq = get_completing_sequences(line)
        if seq is not None: # if not corrupted line
            score.append(get_score(seq))

    score.sort()
    return score[len(score) // 2] # return middle score

print('Part 2:', solve_part2(input))
