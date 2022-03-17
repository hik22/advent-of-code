import java.util.*;
import java.util.function.*;
import java.io.*;

class Coord {
  final static private int dim = 3;
  private int[] data;

  public Coord(int[] data) {
    this.data = data;
  }

  public int get(int i) {
    return data[i];
  }

  static public Coord add(Coord c1, Coord c2) {
    int[] ndata = new int[dim];
    for (int i = 0; i < dim; ++i) {
      ndata[i] = c1.get(i) + c2.get(i);
    }
    return new Coord(ndata);
  }

  static public Coord subtract(Coord c1, Coord c2) {
    int[] ndata = new int[dim];
    for (int i = 0; i < dim; ++i) {
      ndata[i] = c1.get(i) - c2.get(i);
    }
    return new Coord(ndata);
  }

  static public int getSquaredDistance(Coord c1, Coord c2) {
    int dist = 0;
    for (int i = 0; i < dim; ++i) {
      int d = c1.get(i) - c2.get(i);
      dist += d*d;
    }
    return dist;
  }

  static public int getManhattanDistance(Coord c1, Coord c2) {
    int dist = 0;
    for (int i = 0; i < dim; ++i) {
      dist += Math.abs(c1.get(i) - c2.get(i));
    }
    return dist;
  }

  // override equals() and hashCode() to use hashSet
  @Override
  public boolean equals(Object that) {
    if (this == that) {
      return true;
    }
    if (!(that instanceof Coord)) {
      return false;
    }
    Coord t = (Coord) that;
    for (int i = 0; i < dim; ++i) {
      if (this.data[i] != t.data[i]) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(data);
  }
}

class Scanner {
  private List<Coord> coords; // beacon coordinates
  private Coord location;

  public Scanner() {
    coords = new ArrayList<>();
    location = null;
  }

  public void addBeacon(Coord c) {
    coords.add(c);
  }

  public Coord getBeacon(int i) {
    return coords.get(i);
  }

  public int size() {
    return coords.size();
  }

  public void setLocation(Coord c) {
    location = c;
  }

  public Coord getLocation() {
    return location;
  }
}

class Relation {
  public boolean overlapped;
  public Scanner alignedSecond;
  public Coord secondRelativeCoord;

  public Relation(boolean overlapped, Scanner s, Coord c) {
    this.overlapped = overlapped;
    this.alignedSecond = s;
    this.secondRelativeCoord = c;
  }
}

public class Solve {
  final static private String[] ROT_STR = new String[] {
    "x,y,z", "-x,-y,z", "y,-x,z", "-y,x,z",
    "y,z,x", "-y,-z,x", "z,-y,x", "-z,y,x",
    "z,x,y", "-z,-x,y", "x,-z,y", "-x,z,y",
    "y,x,-z", "-y,-x,-z", "x,-y,-z", "-x,y,-z",
    "x,z,-y", "-x,-z,-y", "z,-x,-y", "-z,x,-y",
    "z,y,-x", "-z,-y,-x", "y,-z,-x", "-y,z,-x"
  };

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

    // read input
    List<Scanner> scanners = getScannersFromInput(input);

    // get list of distances for scanners
    List<List<Integer>> dists = new ArrayList<>();
    for (int i = 0; i < scanners.size(); ++i) { // for each scanner
      List<Integer> list = new ArrayList<>();

      // get distance for every pair of beacons
      Scanner s = scanners.get(i);
      for (int j = 0; j < s.size(); ++j) {
        for (int k = j+1; k < s.size(); ++k) {
          list.add(Coord.getSquaredDistance(s.getBeacon(j), s.getBeacon(k)));
        }
      }
      dists.add(list);
    }

    Set<Coord> beacons = new HashSet<>(); // all beacon coordinates
    boolean[] overlapped = new boolean[scanners.size()];

    // set scanner 0 location as origin
    scanners.get(0).setLocation(new Coord(new int[] {0, 0, 0}));

    // find adjacent scanners starting from scanner 0
    Deque<Integer> stack = new LinkedList<>();
    stack.push(0);

    while (stack.size() > 0) {
      int pop = stack.pop();
      Scanner s1 = scanners.get(pop);

      // register all beacon coordinates
      // note that all coordinates are converted and relative to scanner 0
      addBeaconsToSet(beacons, s1);

      // prevent further visit
      overlapped[pop] = true;

      for (int i = 0; i < scanners.size(); ++i) {
        if (overlapped[i]) {
          continue;
        }
        Scanner s2 = scanners.get(i);

        // skip if common distances are not enough
        if (getCommonDistNum(dists.get(pop), dists.get(i)) < 66) {
          continue;
        }

        Relation r = getRelation(s1, s2);
        if (r.overlapped) {
          Scanner ns2 = r.alignedSecond; // coordinates are relative to s1
          ns2.setLocation(r.secondRelativeCoord);
          scanners.set(i, ns2); // update

          stack.push(i);
        }
      }
    }
    System.out.println("Part 1: " + beacons.size());

    // get maximum Manhattan distance for every pair of scanners
    int max = 0;
    for (int i = 0; i < scanners.size(); ++i) {
      for (int j = i+1; j < scanners.size(); ++j) {
        Coord c1 = scanners.get(i).getLocation();
        Coord c2 = scanners.get(j).getLocation();
        int dist = Coord.getManhattanDistance(c1, c2);
        if (dist > max) {
          max = dist;
        }
      }
    }
    System.out.println("Part 2: " + max);
  }

  private static List<Scanner> getScannersFromInput(String[] input) {
    List<Scanner> scanners = new LinkedList<>();
    Scanner s = null;
    for (int i = 0; i < input.length; ++i) { // read each line
      String line = input[i];

      // make a new scanner
      if (line.startsWith("---")) {
        s = new Scanner();
        continue;
      }

      // add scanner to container
      if (line.equals("")) {
        scanners.add(s);
        continue;
      }

      // add coordinate to scanner
      int[] data = Arrays.stream(line.split(","))
        .mapToInt(Integer::parseInt)
        .toArray();
      s.addBeacon(new Coord(data));

      // add scanner to container if last line
      if (i == input.length-1) {
        scanners.add(s);
      }
    }

    return scanners;
  }

  // return the number of common distances
  private static int getCommonDistNum(List<Integer> d1, List<Integer> d2) {
    int count = 0;
    boolean[] d2counted = new boolean[d2.size()];

    for (int i = 0; i < d1.size(); ++i) {
      for (int j = 0; j < d2.size(); ++j) {
        if (d2counted[j]) {
          continue;
        }
        if (d1.get(i).equals(d2.get(j))) {
          d2counted[j] = true;
          ++count;
          break;
        }
      }
    }
    return count;
  }

  // add all beacons in scanner to set of beacons
  private static void addBeaconsToSet(Set<Coord> beacons, Scanner s) {
    for (int i = 0; i < s.size(); ++i) {
      beacons.add(s.getBeacon(i));
    }
  }

  private static Relation getRelation(Scanner s1, Scanner s2) {
    for (int i = 0; i < s1.size(); ++i) {
      Coord ref1 = s1.getBeacon(i); // reference beacon in s1

      for (int j = 0; j < ROT_STR.length; ++j) { // for each rotation
        Scanner rotated2 = getRotatedCopy(s2, ROT_STR[j]);

        for (int k = 0; k < rotated2.size(); ++k) { // for each rotated s2
          Coord ref2 = rotated2.getBeacon(k); // reference beacon in s2

          // shift rotated s2 by difference between two reference beacons
          Coord diff = Coord.subtract(ref1, ref2);
          Scanner shifted2 = getShiftedCopy(rotated2, diff);

          // return if there are enough overlapped beacons
          int n = getOverlappedCoordsNum(s1, shifted2);
          if (n >= 12) {
            // shifted2 has coordinates relative to s1
            return new Relation(true, shifted2, diff);
          }
        }
      }
    }
    return new Relation(false, null, null);
  }

  private static int getOverlappedCoordsNum(Scanner s1, Scanner s2) {
    int count = 0;
    for (int i = 0; i < s1.size(); ++i) {
      for (int j = 0; j < s2.size(); ++j) {
        if (s1.getBeacon(i).equals(s2.getBeacon(j))) {
          ++count;
          break;
        }
      }
    }
    return count;
  }

  private static Scanner getShiftedCopy(Scanner s, Coord shift) {
    Scanner ns = new Scanner();

    for (int i = 0; i < s.size(); ++i) {
      // get shifted coordinate
      Coord nc = Coord.add(s.getBeacon(i), shift);
      ns.addBeacon(nc);
    }
    return ns;
  }

  private static Scanner getRotatedCopy(Scanner s, String rotStr) {
    List<Function<Coord, Integer>> f = new ArrayList<>();
    Scanner ns = new Scanner();

    // rotStr: formatted like "y,-x,z"
    String[] x = rotStr.split(",");
    for (int i = 0; i < s.size(); ++i) {
      Coord b = s.getBeacon(i); // for each beacon coordinate

      // get rotated coordinate
      int[] rotated = new int[3];
      for (int j = 0; j < x.length; ++j) {
        switch (x[j]) {
          case "x":
            rotated[j] = b.get(0);
            break;
          case "y":
            rotated[j] = b.get(1);
            break;
          case "z":
            rotated[j] = b.get(2);
            break;
          case "-x":
            rotated[j] = -b.get(0);
            break;
          case "-y":
            rotated[j] = -b.get(1);
            break;
          case "-z":
            rotated[j] = -b.get(2);
            break;
        }
      }

      ns.addBeacon(new Coord(rotated));
    }
    return ns;
  }
}
