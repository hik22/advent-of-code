import java.util.*;
import java.io.*;

public class Solve {
  public static long solve(String template, String[] rulesStr, int steps) {
    // count pairs
    Map<String, Long> pairs = new HashMap<>();
    for (int i = 0; i < template.length()-1; ++i) {
      String pair = template.substring(i, i+2);

      if (pairs.containsKey(pair)) {
        pairs.put(pair, pairs.get(pair) + 1);
      } else {
        pairs.put(pair, Long.valueOf(1));
      }
    }

    // count characters
    Map<String, Long> counts = new HashMap<>();
    for (int i = 0; i < template.length(); ++i) {
      String c = template.substring(i, i+1);

      if (counts.containsKey(c)) {
        counts.put(c, counts.get(c) + 1);
      } else {
        counts.put(c, Long.valueOf(1));
      }
    }

    // parse rule strings
    Map<String, String> rules = new HashMap<>();
    for (String line : rulesStr) {
      String[] s = line.split(" -> ");
      String key = s[0];
      String value = s[1];

      rules.put(key, value);
    }

    // get next state of sequence
    for (int i = 0; i < steps; ++i) {
      Map<String, Long> newPairs = new HashMap<>();
      for (String oldPair : pairs.keySet()) {
        String mid = rules.get(oldPair);
        Long n = pairs.get(oldPair);

        String newPair1 = oldPair.substring(0, 1).concat(mid);
        String newPair2 = mid.concat(oldPair.substring(1, 2));

        // get next pairs
        if (newPairs.containsKey(newPair1)) {
          newPairs.put(newPair1, newPairs.get(newPair1) + Long.valueOf(n));
        } else {
          newPairs.put(newPair1, n);
        }
        if (newPairs.containsKey(newPair2)) {
          newPairs.put(newPair2, newPairs.get(newPair2) + Long.valueOf(n));
        } else {
          newPairs.put(newPair2, n);
        }

        // update counts
        if (counts.containsKey(mid)) {
          counts.put(mid, counts.get(mid) + n);
        } else {
          counts.put(mid, Long.valueOf(n));
        }
      }
      pairs = newPairs;
    }

    // get answer
    Long max = Collections.max(counts.values());
    Long min = Collections.min(counts.values());
    return max - min;
  }

  public static long solvePart1(String template, String[] rulesStr) {
    return solve(template, rulesStr, 10);
  }

  public static long solvePart2(String template, String[] rulesStr) {
    return solve(template, rulesStr, 40);
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.out.println("Usage: java Solve <input-file-name>");
      System.exit(1);
    }

    // read file input
    String fname = args[0];
    String[] input;
    try (BufferedReader b = new BufferedReader(new FileReader(fname))) {
      input = b.lines().toArray(String[]::new);
    }

    // split input into two parts
    int i = 0;
    while (!input[i].equals("")) {
      ++i;
    }
    String template = input[0];
    String[] rules = Arrays.copyOfRange(input, i+1, input.length);

    System.out.println("Part 1: " + solvePart1(template, rules));
    System.out.println("Part 2: " + solvePart2(template, rules));
  }
}
