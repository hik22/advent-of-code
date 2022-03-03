import sys
if len(sys.argv) != 2:
    print('Usage: python solve.py <input-file-name>')
    sys.exit(1)

fname = sys.argv[1]
input = [line.strip() for line in open(fname)]
nums = [[int(str) for str in line] for line in input]

def is_low_point(x, y, nums):
    xmax = len(nums[0])-1
    ymax = len(nums)-1
    cur = nums[y][x]

    if x > 0 and nums[y][x-1] <= cur:
        return False
    if x < xmax and nums[y][x+1] <= cur:
        return False
    if y > 0 and nums[y-1][x] <= cur:
        return False
    if y < ymax and nums[y+1][x] <= cur:
        return False
    return True

def solve_part1(nums):
    width = len(nums[0])
    height = len(nums)

    # find height for each low point
    lows = []
    for y in range(height):
        for x in range(width):
            if is_low_point(x, y, nums):
                lows.append(nums[y][x])

    risk_level = sum(lows) + len(lows)
    return risk_level

print('Part 1:', solve_part1(nums))

def get_basin_containing_pos(x, y, nums):
    xmax = len(nums[0]) - 1
    ymax = len(nums) - 1

    basin = []
    set = {(x, y)}

    while len(set) > 0:
        x, y = set.pop()

        basin.append(nums[y][x])
        nums[y][x] = 9

        if y > 0 and nums[y-1][x] != 9:
            set.add((x, y-1))
        if x > 0 and nums[y][x-1] != 9:
            set.add((x-1, y))
        if y < ymax and nums[y+1][x] != 9:
            set.add((x, y+1))
        if x < xmax and nums[y][x+1] != 9:
            set.add((x+1, y))

    return basin

def solve_part2(nums):
    width = len(nums[0])
    height = len(nums)

    lens = []
    for y in range(height):
        for x in range(width):
            cur = nums[y][x]

            if cur != 9:
                basin = get_basin_containing_pos(x, y, nums)
                lens.append(len(basin))

    lens.sort()
    return lens[-1] * lens[-2] * lens[-3]

print('Part 2:', solve_part2(nums))
