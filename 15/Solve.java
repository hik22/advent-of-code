import java.util.*;
import java.io.*;

public class Solve {
  public static int getOffset(int x, int y, int w, int h) {
    return x / w + y / h;
  }

  public static int solve(int[][] data, int w, int h) {
    class Item implements Comparable<Item> {
      private Integer risk;
      private Integer x;
      private Integer y;

      public Item(Integer risk, Integer x, Integer y) {
        this.risk = risk;
        this.x = x;
        this.y = y;
      }
      public Integer getRisk() {
        return risk;
      }
      public Integer getX() {
        return x;
      }
      public Integer getY() {
        return y;
      }

      public int compareTo(Item that) {
        return this.risk.compareTo(that.getRisk());
      }
    }

    boolean visited[][] = new boolean[h][w];

    // get path
    Integer ans = 0;
    PriorityQueue<Item> pq = new PriorityQueue<>();
    pq.add(new Item(0, 0, 0));
    while (pq.size() > 0) {
      Item item = pq.poll();
      Integer risk = item.getRisk();
      Integer x = item.getX();
      Integer y = item.getY();

      if (x == w-1 && y == h-1) {
        ans = risk;
        break;
      }
      if (visited[y][x]) {
        continue; // skip
      }
      visited[y][x] = true;

      if (x > 0) {
        pq.add(new Item(risk + data[y][x-1], x-1, y));
      }
      if (x < w-1) {
        pq.add(new Item(risk + data[y][x+1], x+1, y));
      }
      if (y > 0) {
        pq.add(new Item(risk + data[y-1][x], x, y-1));
      }
      if (y < h-1) {
        pq.add(new Item(risk + data[y+1][x], x, y+1));
      }
    }
    return ans;
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

    // initialize constants for part 1
    final int h = input.length;
    final int w = input[0].length();

    // convert string array into int array
    int data[][] = new int[h][w];
    for (int i = 0; i < h; ++i) {
      for (int j = 0; j < w; ++j) {
        String c = Character.toString(input[i].charAt(j));
        int n = Integer.parseInt(c);
        data[i][j] = n;
      }
    }

    System.out.println("Part 1: " + solve(data, w, h));

    // initialize constants for part 2
    final int nh = h*5;
    final int nw = w*5;

    // get map for part 2 using given input
    int newdata[][] = new int[nh][nw];
    for (int y = 0; y < nh; ++y) {
      for (int x = 0; x < nw; ++x) {
        int newrisk = data[y % w][x % h] + getOffset(x, y, w, h);
        if (newrisk > 9) {
          newrisk = newrisk % 10 + 1;
        }
        newdata[y][x] = newrisk;
      }
    }

    System.out.println("Part 2: " + solve(newdata, w*5, h*5));
  }
}
