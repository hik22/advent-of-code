import java.util.*;
import java.io.*;
import java.util.stream.Collectors;

class Coord {
  private int x;
  private int y;

  public Coord(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return this.x;
  }

  public int getY() {
    return this.y;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Coord) ) {
      return false;
    }
    Coord that = (Coord) obj;
    return (this.x == that.x && this.y == that.y) ? true : false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }

  @Override
  public String toString() {
    return "(" + x + ", " + y + ")";
  }
}

public class Solve {
  public static void main(String[] args) throws IOException {
    // exit if no command line argument
    if (args.length != 1) {
      System.out.println("Usage: java Solve <input-file-name>");
      System.exit(1);
    }

    // read file input
    String fname = args[0];
    String ieaStr = null; // image enhancement algorithm
    String[] imgStr = null;
    try (BufferedReader b = new BufferedReader(new FileReader(fname))) {
      String input = b.lines().collect(Collectors.joining("\n"));
      String[] split = input.split("\n\n");
      ieaStr = split[0];
      imgStr = split[1].split("\n");
    }

    // create an image enhancement algorithm array
    String[] iea = new String[ieaStr.length()];
    for (int i = 0; i < ieaStr.length(); ++i) {
      iea[i] = ieaStr.substring(i, i+1);
    }

    // get image size
    int xbeg = 0;
    int xend = imgStr[0].length();
    int ybeg = 0;
    int yend = imgStr.length;

    // create a map object using input image
    Map<Coord, String> image = new HashMap<>();
    for (int y = ybeg; y < yend; ++y) {
      for (int x = xbeg; x < xend; ++x) {
        Coord coord = new Coord(x, y);
        image.put(coord, imgStr[y].substring(x, x+1));
      }
    }

    // set image pixel beyond detected image size
    String defaultPixel = ".";

    for (int step = 0; step < 50; ++step) {
      Map<Coord, String> nimage = new HashMap<>();

      // increase image size to cover by one
      --xbeg;
      --ybeg;
      ++xend;
      ++yend;

      // apply for each pixel in detected size
      for (int y = ybeg; y < yend; ++y) {
        for (int x = xbeg; x < xend; ++x) {
          int idx = getIEAIndexFromPixel(x, y, image, defaultPixel);
          String res = iea[idx];
          nimage.put(new Coord(x, y), res);
        }
      }

      // apply for pixels beyond size
      int idx = defaultPixel.equals(".") ? 0 : 511;
      defaultPixel = iea[idx];

      // update image
      image = nimage;

      // print result
      if (step == 1) {
        System.out.println("Part 1: " + countLightPixels(image));
      }
      if (step == 49) {
        System.out.println("Part 2: " + countLightPixels(image));
      }
    }
  }

  public static int countLightPixels(Map<Coord, String> image) {
    int count = 0;
    for (String pixel : image.values()) {
      if (pixel.equals("#")) {
        ++count;
      }
    }
    return count;
  }

  public static int getIEAIndexFromPixel(
      int px, int py, Map<Coord, String> image, String defaultPixel) {
    int xbeg = px-1;
    int ybeg = py-1;
    int xend = px+2; // past-the-end position
    int yend = py+2;

    int idx = 0;
    for (int y = ybeg; y < yend; ++y) {
      for (int x = xbeg; x < xend; ++x) {
        idx <<= 1;

        // get pixel
        Coord coord = new Coord(x, y);
        String pixel = null;
        if (image.containsKey(coord)) {
          pixel = image.get(coord);
        } else {
          pixel = defaultPixel;
        }

        // increase by one if light pixel found
        if (pixel.equals("#")) {
          ++idx;
        }
      }
    }
    return idx;
  }
}
