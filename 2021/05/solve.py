from collections import defaultdict
import sys
if len(sys.argv) != 2:
    print('Usage: python solve.py <input-file-name>')
    sys.exit(1)

fname = sys.argv[1]
input = [line.strip() for line in open(fname)]

def parse_line(line):
    p1, p2 = line.split(' -> ')
    x1, y1 = [int(str) for str in p1.split(',')]
    x2, y2 = [int(str) for str in p2.split(',')]
    return x1, y1, x2, y2

def get_lines(input):
    lines = []
    for line in input:
        x1, y1, x2, y2 = parse_line(line)
        lines.append({'x1': x1, 'y1': y1, 'x2': x2, 'y2': y2})
    return lines

def get_interval(x1, x2):
    if x1 > x2:
        xs = list(range(x1, x2-1, -1))
    else:
        xs = list(range(x1, x2+1))

    return xs

def draw(x1, y1, x2, y2, overlap):
    # make intervals
    xs = get_interval(x1, x2)
    ys = get_interval(y1, y2)

    # make length same
    if len(xs) == 1:
        xs = xs * len(ys)
    elif len(ys) == 1:
        ys = ys * len(xs)

    # increase by 1 for each point
    for point in zip(xs, ys):
        overlap[point] += 1

    return overlap

def solve_part1(input):
    # get lines
    lines = get_lines(input)

    # filter out diagonal lines
    lines = list(filter(lambda d: d['x1'] == d['x2'] or d['y1'] == d['y2'], lines))

    overlap = defaultdict(int)
    for line in lines:
        x1, y1, x2, y2 = line['x1'], line['y1'], line['x2'], line['y2']
        overlap = draw(x1, y1, x2, y2, overlap)

    count = len(overlap) - list(overlap.values()).count(1)
    return count

print('Part 1:', solve_part1(input))

def solve_part2(lines):
    # get lines
    lines = get_lines(input)

    overlap = defaultdict(int)
    for line in lines:
        x1, y1, x2, y2 = line['x1'], line['y1'], line['x2'], line['y2']
        overlap = draw(x1, y1, x2, y2, overlap)

    count = len(overlap) - list(overlap.values()).count(1)
    return count

print('Part 2:', solve_part2(input))
