import sys
if len(sys.argv) != 2:
    print('Usage: python solve.py <input-file-name>')
    sys.exit(1)

fname = sys.argv[1]
input = [int(str) for str in open(fname)]

# part 1
prev = None
count = 0
for depth in input:
    if prev is not None and depth > prev:
        count += 1
    prev = depth
print('Part 1:', count)

# part 2
windows = []
for i in range(len(input)-2):
    windows.append(input[i] + input[i+1] + input[i+2])

prev = None
count = 0
for depth in windows:
    if prev is not None and depth > prev:
        count += 1
    prev = depth
print('Part 2:', count)
