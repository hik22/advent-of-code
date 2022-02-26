from functools import reduce
import sys
if len(sys.argv) != 2:
    print('Usage: python solve.py <input-file-name>')
    sys.exit(1)

fname = sys.argv[1]
input = [line.strip() for line in open(fname)]

def solve_part1(input):
    count = 0
    for line in input:
        pat, output = line.split(' | ')
        digits = output.split()
        length = map(len, digits)

        # count 2, 3, 4, 7
        # 2 for 1, 3 for 7, 4 for 4, 7 for 8
        for l in length:
            if l == 2 or l == 3 or l == 4 or l == 7:
                count += 1

    return count

print('Part 1:', solve_part1(input))

def solve_part2(input):
    # add output value to sum for each input line
    sum = 0
    for line in input:
        pats, output = line.split(' | ')

        # read patterns
        one, four, seven, eight = None, None, None, None
        seg5, seg6 = [], [] # digits that requires 5 and 6 segments
        for pat in pats.split():
            if len(pat) == 2:
                one = set(pat)
            elif len(pat) == 3:
                seven = set(pat)
            elif len(pat) == 4:
                four = set(pat)
            elif len(pat) == 5:
                # 5 segments group
                seg5.append(set(pat))
            elif len(pat) == 6:
                # 6 segments group
                seg6.append(set(pat))
            elif len(pat) == 7:
                eight = set(pat)

        # get segment a signal
        a_sig = (seven - one).pop()

        # intersection of 5 segments group: a, d, g signals
        inter5 = reduce(lambda x, y: x & y, seg5)
        # intersection of 6 segments group: a, b, f, g signals
        inter6 = reduce(lambda x, y: x & y, seg6)
        # intersection of two sets above: a, g signals
        inter = inter5 & inter6
        # get segment g signal
        g_sig = (inter - set(a_sig)).pop()

        # get a set of b, d, f signals
        tmp1 = (inter5 | inter6) - (inter5 & inter6)
        # get a set of b, d signals
        tmp2 = four - one
        # get segment f signal
        f_sig = (tmp1 - tmp2).pop()

        # get segment c signal
        c_sig = (one - set(f_sig)).pop()

        # get digit three
        acfg = set([a_sig, c_sig, f_sig, g_sig])
        three = None
        for digit in seg5:
            if acfg < digit: # acfg is subset
                three = digit
        # get segment d signal
        d_sig = (three - set([a_sig, c_sig, f_sig, g_sig])).pop()

        # get digit two
        acdg = set([a_sig, c_sig, d_sig, g_sig])
        two = None
        for digit in seg5:
            if acdg < digit and digit != three:
                two = digit
        # get segment e signal
        e_sig = (two - acdg).pop()

        # get segment b signal
        b_sig = (four - set([d_sig, c_sig, f_sig])).pop()

        # get remained digits
        zero = eight - set(d_sig)
        five = eight - set([c_sig, e_sig])
        six = eight - set(c_sig)
        nine = eight - set(e_sig)

        # read four digit output and get its value
        output_digits = []
        for d in output.split():
            sd = set(d)
            a = None
            if zero == sd:
                a = '0'
            elif one == sd:
                a = '1'
            elif two == sd:
                a = '2'
            elif three == sd:
                a = '3'
            elif four == sd:
                a = '4'
            elif five == sd:
                a = '5'
            elif six == sd:
                a = '6'
            elif seven == sd:
                a = '7'
            elif eight == sd:
                a = '8'
            elif nine == sd:
                a = '9'
            output_digits.append(a)

        # add the value to sum
        sum += int(''.join(output_digits))

    return sum

print('Part 2:', solve_part2(input))
