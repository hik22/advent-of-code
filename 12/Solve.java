import java.util.*;
import java.io.*;

public class Solve {
  public static boolean isLowerCase(String s) {
    for (int i = 0; i < s.length(); ++i) {
      if (Character.isUpperCase(s.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  public static int countPath1(
      HashMap<String, LinkedList<String>> map,
      String cur,
      LinkedList<String> smallCaves) {
    if (cur.equals("end")) {
      return 1;
    }

    int count = 0;
    for (String cave : map.get(cur)) {
      if (isLowerCase(cave) && smallCaves.contains(cave)) {
        continue;
      }

      if (isLowerCase(cave)) {
        smallCaves.push(cave);
      }
      count += countPath1(map, cave, smallCaves);
      if (isLowerCase(cave)) {
        smallCaves.pop();
      }
    }
    return count;
  }

  public static int countPath2(
      HashMap<String, LinkedList<String>> map,
      String cur,
      LinkedList<String> smallCaves,
      LinkedList<String> exceptCave) {
    if (cur.equals("end")) {
      return 1;
    }

    int count = 0;
    for (String cave : map.get(cur)) {
      if (isLowerCase(cave) && smallCaves.contains(cave) &&
          exceptCave.size() > 0) {
        continue;
      }
      if (cave.equals("start")) {
        continue;
      }

      int flag = 0;
      if (isLowerCase(cave) && smallCaves.contains(cave)) {
        exceptCave.push(cave);
        flag = 1;
      } else if (isLowerCase(cave) && !smallCaves.contains(cave)) {
        smallCaves.push(cave);
        flag = 2;
      }
      count += countPath2(map, cave, smallCaves, exceptCave);
      if (flag == 2) {
        smallCaves.pop();
      } else if (flag == 1) {
        exceptCave.pop();
      }
    }
    return count;
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

    // parse input into map
    HashMap<String, LinkedList<String>> map = new HashMap<>();
    for (String line : input) {
      String[] caves = line.split("-");

      // connect
      if (!map.containsKey(caves[0])) {
        map.put(caves[0], new LinkedList<String>());
      }
      map.get(caves[0]).push(caves[1]);
      if (!map.containsKey(caves[1])) {
        map.put(caves[1], new LinkedList<String>());
      }
      map.get(caves[1]).push(caves[0]);
    }

    LinkedList<String> smallCaves = new LinkedList<>();
    smallCaves.push("start");
    int count = countPath1(map, "start", smallCaves);
    System.out.println("Part 1: " + count);

    LinkedList<String> exceptCave = new LinkedList<>();
    count = countPath2(map, "start", smallCaves, exceptCave);
    System.out.println("Part 2: " + count);
  }
}
