import java.util.*;
import java.io.*;
import java.util.stream.Collectors;

public class Solve {
  // assume x1 < x2, y1 < y2
  public static boolean isInArea(int vx, int vy, int x1, int x2, int y1, int y2) {
    int x = 0;
    int y = 0;
    while (true) {
      x += vx;
      y += vy;

      // update velocities
      if (vx > 0) {
        --vx;
      } else if (vx < 0) {
        ++vx;
      }
      --vy;

      if (x1 <= x && x <= x2 && y1 <= y && y <= y2) {
        return true;
      }
      if (x > x2 || y < y1) {
        return false;
      }
    }
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.out.println("Usage: java Solve <input-file-name>");
      System.exit(1);
    }

    // read file input
    String fname = args[0];
    String input;
    try (BufferedReader b = new BufferedReader(new FileReader(fname))) {
      input = b.lines().collect(Collectors.joining(""));
    }

    // assume y's of the area are below zero
    String[] s = input.substring(13).split(", ");
    String[] xs = s[0].substring(2).split("\\.\\.");
    String[] ys = s[1].substring(2).split("\\.\\.");
    int x1 = Integer.parseInt(xs[0]);
    int x2 = Integer.parseInt(xs[1]);
    int y1 = Integer.parseInt(ys[0]);
    int y2 = Integer.parseInt(ys[1]);

    // solve part 1
    int ans1 = (-y1 - 1) * (-y1) / 2;
    System.out.println("Part 1: " + ans1);

    // solve part 2
    int vy_min = y1;
    int vy_max = -y1 - 1;
    int vx_max = x2;
    int vx_min = (int) Math.ceil(Math.sqrt(2 * x1 + 0.25) - 0.5);

    // count velocity that causes the probe to be in the target area
    int count = 0;
    for (int vy = vy_min; vy <= vy_max; ++vy) {
      for (int vx = vx_min; vx <= vx_max; ++vx) { // for each velocity
        if (isInArea(vx, vy, x1, x2, y1, y2)) {
          ++count;
        }
      }
    }
    System.out.println("Part 2: " + count);
  }
}
