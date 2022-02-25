import sys
if len(sys.argv) != 2:
    print('Usage: python solve.py <input-file-name>')
    sys.exit(1)

fname = sys.argv[1]
input1, *input2= [line.strip() for line in open(fname).read().split('\n\n')]

# part 1

# get numbers
nums = [int(str) for str in input1.split(',')]

boards = []
for chunk in input2:
    board = [] # buffer
    for line in chunk.split('\n'):
        board.append([int(str) for str in line.split()])
    boards.append(board)

# initialize marked positions for each board
marked_all = []
for _ in range(len(boards)):
    marked_all.append([[False for _ in range(5)] for _ in range(5)])

def mark(num, board, marked):
    # mark number
    for i in range(len(board)): # for each row
        if num in board[i]:
            j = board[i].index(num)
            marked[i][j] = True
            return True

    return False # marking failed

def is_row_bingo(marked):
    for i in range(5):
        if marked[i].count(True) == 5:
            return True
    return False

def is_col_bingo(marked):
    for i in range(5): # column index i
        is_bingo = True
        for j in range(5): # row index j
            if not marked[j][i]:
                is_bingo = False
                break

        if is_bingo:
            return True
    return False

def get_sum_of_unmarked(board, marked):
    sum = 0
    for i in range(5): # row
        for j in range(5): # column
            if not marked[i][j]:
                sum += board[i][j]
    return sum

result = None
for num in nums:
    is_bingo = False

    # mark number for each board
    for i in range(len(boards)):
        mark(num, boards[i], marked_all[i])

    # check bingo
    for i in range(len(boards)): # for i-th board
        if is_row_bingo(marked_all[i]) or is_col_bingo(marked_all[i]):
            sum = get_sum_of_unmarked(boards[i], marked_all[i])
            result = sum * num
            is_bingo = True
            break

    if is_bingo:
        break

print('Part 1:', result)

# part 2

# initialize marked positions for each board
marked_all = []
for _ in range(len(boards)):
    marked_all.append([[False for _ in range(5)] for _ in range(5)])

playing = [i for i in range(len(boards))]

result = None
for num in nums:
    # mark number for each board
    for i in playing:
        mark(num, boards[i], marked_all[i])

    # check bingo
    for i in playing:
        if is_row_bingo(marked_all[i]) or is_col_bingo(marked_all[i]):
            playing.remove(i)
            if len(playing) == 0: # last board
                sum = get_sum_of_unmarked(boards[i], marked_all[i])
                result = sum * num
                break

    if len(playing) == 0:
        break

print('Part 2:', result)
