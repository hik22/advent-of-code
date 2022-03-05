import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Solve {
  // factorized and structured as follows:
  //   getNextOctopusesToFlash() -> flash() -> resetEnergy(), for each step

  // input array width and height
  static int w, h;

  public static int getNextOctopusesToFlash(int[][] data, ArrayList<int[]> stack) {
    // count flash
    int count = 0;

    // increase energy level by 1
    for (int i = 0; i < h; ++i) {
      for (int j = 0; j < w; ++j) {
        data[i][j] += 1;

        if (data[i][j] > 9) { // if octopus to flash
          stack.add(new int[] {i, j}); // push it to stack
          data[i][j] = -1; // mark as flashed
          ++count; // count flash
        }
      }
    }
    return count;
  }

  public static int flash(int[][] data, ArrayList<int[]> stack) {
    // count flash
    int count = 0;

    // flash
    int n;
    while ((n = stack.size()) > 0) {
      // get position of octopus to flash
      int[] pop = stack.remove(n-1);
      int y = pop[0];
      int x = pop[1];

      // get ranges of adjacent octopuses
      int ymin = Math.max(0, y-1);
      int xmin = Math.max(0, x-1);
      int ymax = Math.min(h-1, y+1);
      int xmax = Math.min(w-1, x+1);

      // increase energy of adjacent octopuses by 1
      for (int ny = ymin; ny <= ymax; ++ny) {
        for (int nx = xmin; nx <= xmax; ++nx) {
          if (ny == y && nx == x) { // skip current position
            continue;
          }

          if (data[ny][nx] != -1) { // if not marked as flashed
            data[ny][nx] += 1;
          }

          if (data[ny][nx] > 9) { // if octopus to flash
            stack.add(new int[] {ny, nx}); // push it to stack
            data[ny][nx] = -1; // mark as flashed
            ++count; // count flash
          }
        }
      }
    }
    return count;
  }

  static int resetEnergy(int[][] data) {
    // reset to zero for octopuses that flashed
    int c = 0; // count flashed octopuses
    for (int i = 0; i < h; ++i) {
      for (int j = 0; j < w; ++j) {
        if (data[i][j] == -1) { // if marked as flashed
          data[i][j] = 0;
          ++c;
        }
      }
    }
    return c;
  }

  public static int solvePart1(int[][] data) {
    // count flashes
    int count = 0;

    for (int step = 0; step < 100; ++step) {
      // initialize stack to save positions of octopuses to flash
      ArrayList<int[]> toflash = new ArrayList<>();

      count += getNextOctopusesToFlash(data, toflash);
      count += flash(data, toflash);
      resetEnergy(data);
    }
    return count;
  }

  public static int solvePart2(int[][] data) {
    int step = 1;
    while (true) {
      // initialize stack to save positions of octopuses to flash
      ArrayList<int[]> toflash = new ArrayList<>();

      getNextOctopusesToFlash(data, toflash);
      flash(data, toflash);

      // return step number if all flashed
      int count = resetEnergy(data);
      if (count == 100) {
        return step;
      }
      ++step;
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

    // initialize constants
    h = input.length;
    w = input[0].length();

    // convert string array into int array
    int data[][] = new int[h][w];
    for (int i = 0; i < h; ++i) {
      for (int j = 0; j < w; ++j) {
        String c = Character.toString(input[i].charAt(j));
        int n = Integer.parseInt(c);
        data[i][j] = n;
      }
    }

    int[][] copy;
    copy = Arrays.stream(data).map(int[]::clone).toArray(int[][]::new);
    System.out.println("Part 1: " + solvePart1(copy));

    copy = Arrays.stream(data).map(int[]::clone).toArray(int[][]::new);
    System.out.println("Part 2: " + solvePart2(copy));
  }
}
