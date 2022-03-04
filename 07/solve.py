from collections import Counter, defaultdict
import sys
if len(sys.argv) != 2:
    print('Usage: python solve.py <input-file-name>')
    sys.exit(1)

fname = sys.argv[1]
input = [int(str) for str in open(fname).read().strip().split(',')]

def get_total_fuel(input, callback):
    crabs = Counter(input)
    # get possible positions
    pos = range(min(crabs.keys()), max(crabs.keys()+1))

    # get fuel for each position to find the minimum fuel
    prev_fuel = None
    for i in range(pos_min, pos_max+1):
        fuel = 0
        for pos, num in crabs.items():
            fuel += callback(i, pos) * num

        # return if minimum fuel found
        if prev_fuel is not None and prev_fuel < fuel:
            return prev_fuel
        else:
            prev_fuel = fuel

def solve_part1(input):
    return get_total_fuel(input, lambda i, pos: abs(pos - i))

print('Part 1:', solve_part1(input))

def solve_part2(input):
    def get_fuel_for_pos(i, pos):
        n = abs(pos - i)
        return n*(n+1) // 2

    return get_total_fuel(input, get_fuel_for_pos)

print('Part 2:', solve_part2(input))
