from collections import Counter
import sys
if len(sys.argv) != 2:
    print('Usage: python solve.py <input-file-name>')
    sys.exit(1)

fname = sys.argv[1]
input = [line.strip() for line in open(fname)]

# part 1
def solve_part1(input):
    length = len(input[0])

    gamma, eps = [], [] # buffers
    for i in range(length): # for each column index
        # count 0's and 1's in column
        col = [line[i] for line in input]
        count = Counter(col)

        # write buffers
        if count['0'] > count['1']:
            gamma.append('0')
            eps.append('1')
        elif count['0'] < count['1']:
            gamma.append('1')
            eps.append('0')
        else:
            raise Exception(f'bad count: 0: {count[0]}, 1: {count[1]}')

    # convert buffers into strings
    gamma = ''.join(gamma)
    eps = ''.join(eps)

    # get decimal values
    gamma = int(gamma, 2)
    eps = int(eps, 2)

    return gamma * eps

print('Part 1:', solve_part1(input))

# part 2
def solve_part2(input):
    length = len(input[0])

    # get oxygen generator rating
    oxygen = 0
    copy = input[:]
    for i in range(length): # for each bit position
        # count 0's and 1's in column
        col = [line[i] for line in copy]
        count = Counter(col)

        # get most common value
        most = '1'
        if count['0'] > count['1']:
            most = '0'

        # filter out
        copy = [line for line in copy if line[i] == most]

        if len(copy) == 1:
            oxygen = ''.join(copy)
            oxygen = int(oxygen, 2)
            break

    # get co2 scrubber rating
    co2 = 0
    copy = input[:]
    for i in range(length): # for each bit position
        # count 0's and 1's in column
        col = [line[i] for line in copy]
        count = Counter(col)

        # get least common value
        least = '0'
        if count['0'] > count['1']:
            least = '1'

        # filter out
        copy = [line for line in copy if line[i] == least]

        if len(copy) == 1:
            co2 = ''.join(copy)
            co2 = int(co2, 2)
            break

    return oxygen * co2

# print result
print('Part 2:', solve_part2(input))
