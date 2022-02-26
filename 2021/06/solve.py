from collections import Counter, defaultdict
import sys
if len(sys.argv) != 2:
    print('Usage: python solve.py <input-file-name>')
    sys.exit(1)

fname = sys.argv[1]
# remove commas
input = ''.join(open(fname).read().strip().split(','))
input = Counter(input)
count = {} # dict with interger key
for k, v in input.items():
    count[int(k)] = v

def simulate(count, day):
    for day in range(1, day+1):
        # get next state of number of fishes
        next = defaultdict(int)

        for timer in count.keys():
            if timer in count and timer != 0:
                next[timer-1] += count[timer]
            elif timer in count and timer == 0:
                next[8] += count[timer]
                next[6] += count[timer]
        count = next

    return sum(count.values())

def solve_part1(count):
    return simulate(count, 80)

def solve_part2(count):
    return simulate(count, 256)

print('Part 1:', solve_part1(count))
print('Part 2:', solve_part2(count))
