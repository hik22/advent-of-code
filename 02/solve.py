import sys
if len(sys.argv) != 2:
    print('Usage: python solve.py <input-file-name>')
    sys.exit(1)

fname = sys.argv[1]
input = [line.strip() for line in open(fname)]

# part 1
hpos = 0 # horizontal position
depth = 0
for line in input:
    command, num = line.split()
    num = int(num)

    if command == 'forward':
        hpos += num
    elif command == 'down':
        depth += num
    elif command == 'up':
        depth -= num
    else:
        # unreachable
        raise Exception(f'bad command: {command}')

print('Part 1:', hpos * depth)

# part 2
hpos = 0
depth = 0
aim = 0
for line in input:
    command, num = line.split()
    num = int(num)

    if command == 'forward':
        hpos += num
        depth += aim * num
    elif command == 'down':
        aim += num
    elif command == 'up':
        aim -= num
    else:
        # unreachable
        raise Exception(f'bad command: {command}')

print('Part 2:', hpos * depth)
