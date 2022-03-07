import java.util.*;
import java.io.*;

public class Solve {
  public static Set<List<Integer>> getCoordinates(String[] input1) {
    Set<List<Integer>> set = new HashSet<>();
    for (String line : input1) {
      String[] coord = line.split(",");
      int x = Integer.parseInt(coord[0]);
      int y = Integer.parseInt(coord[1]);

      set.add(Arrays.asList(new Integer[] {x, y}));
    }

    return set;
  }

  public static Set<List<Integer>> getFoldedPaper(
      Set<List<Integer>> set, String axis, int num) {
    Set<List<Integer>> folded = new HashSet<>();

    for (List<Integer> coord : set) {
      int x = coord.get(0);
      int y = coord.get(1);

      // write new (folded) coordinates to 'folded' set
      if (axis.equals("y")) {
        int ny = (y > num) ? y - 2 * (y - num) : y;
        folded.add(Arrays.asList(new Integer[] {x, ny}));
      } else { // axis equals "x"
        int nx = (x > num) ? x - 2 * (x - num) : x;
        folded.add(Arrays.asList(new Integer[] {nx, y}));
      }
    }
    return folded;
  }

  public static int solvePart1(String[] input1, String[] input2) {
    // get point coordinates
    Set<List<Integer>> set = getCoordinates(input1);

    // parse second part of first input line
    String[] split = input2[0].substring(11).split("=");
    String axis = split[0];
    int num = Integer.parseInt(split[1]);

    Set<List<Integer>> folded = getFoldedPaper(set, axis, num);
    return folded.size();
  }

  public static void solvePart2(String[] input1, String[] input2) {
    // get point coordinates
    Set<List<Integer>> set = getCoordinates(input1);

    // fold
    for (int i = 0; i < input2.length; ++i) {
      // parse each line of second part of input
      String[] split = input2[i].substring(11).split("=");
      String axis = split[0];
      int num = Integer.parseInt(split[1]);

      set = getFoldedPaper(set, axis, num);
    }

    // get paper size
    int w = -1, h = -1;
    for (List<Integer> coord : set) {
      int x = coord.get(0);
      int y = coord.get(1);

      // update
      if (x > w) {
        w = x;
      }
      if (y > h) {
        h = y;
      }
    }

    // print
    System.out.println("Part 2:");
    for (int y = 0; y <= h; ++y) {
      for (int x = 0; x <= w; ++x) {
        if (set.contains(Arrays.asList(new Integer[] {x, y}))) {
          System.out.print("#");
        } else {
          System.out.print(".");
        }
      }
      System.out.println();
    }
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
    String[] input1 = Arrays.copyOfRange(input, 0, i);
    String[] input2 = Arrays.copyOfRange(input, i+1, input.length);

    // print answers
    System.out.println("Part 1: " + solvePart1(input1, input2));
    solvePart2(input1, input2);
  }
}
