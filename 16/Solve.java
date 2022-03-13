import java.util.*;
import java.io.*;
import java.util.stream.Collectors;
import java.math.BigInteger;

class Result {
  public int ver;
  public int rp;
  public BigInteger value;

  public Result(int rp, int ver, BigInteger value) {
    this.rp = rp;
    this.ver = ver;
    this.value = value;
  }
}

public class Solve {
  public static String hex2bin(String hex) {
    String bin = "";
    for (int i = 0; i < hex.length(); ++i) {
      String ch = hex.substring(i, i+1);
      Integer n = Integer.parseInt(ch, 16);
      String bits = Integer.toString(n, 2);
      String padded = "0".repeat(4 - bits.length()) + bits;
      bin += padded;
    }
    return bin;
  }

  public static int readPacket(String packet, int rp, int length) {
    return Integer.parseInt(packet.substring(rp, rp+length), 2);
  }

  public static Result parse(String packet, int rp) {
    int ver = readPacket(packet, rp, 3);
    rp += 3;
    int type_id = readPacket(packet, rp, 3);
    rp += 3;

    if (type_id == 4) { // literal value
      String chunk = "";
      while (true) {
        int prefix = readPacket(packet, rp, 1);
        rp += 1;
        chunk += packet.substring(rp, rp+4);
        rp += 4;

        if (prefix == 0) {
          break;
        }
      }
      BigInteger val = new BigInteger(chunk, 2);
      return new Result(rp, ver, val);
    }

    // if operator, read sub-packets using recursion
    int length_type_id = readPacket(packet, rp, 1);
    rp += 1;

    List<BigInteger> values = new ArrayList<>(); // sub-packet values
    if (length_type_id == 0) {
      int length = readPacket(packet, rp, 15);
      rp += 15;

      int dest = rp + length;
      while (rp < dest) { // read sub-packets
        Result res = parse(packet, rp);
        rp = res.rp;
        ver += res.ver;
        values.add(res.value);
      }
    } else if (length_type_id == 1) {
      int num = readPacket(packet, rp, 11);
      rp += 11;

      while (num-- > 0) { // read sub-packets
        Result res = parse(packet, rp);
        rp = res.rp;
        ver += res.ver;
        values.add(res.value);
      }
    }

    // process operation
    if (type_id == 0) { // sum
      BigInteger sum = new BigInteger("0");
      for (int i = 0; i < values.size(); ++i) {
        sum = sum.add(values.get(i));
      }
      return new Result(rp, ver, sum);
    } else if (type_id == 1) { // product
      BigInteger prod = new BigInteger("1");
      for (int i = 0; i < values.size(); ++i) {
        prod = prod.multiply(values.get(i));
      }
      return new Result(rp, ver, prod);
    } else if (type_id == 2) { // minimum
      BigInteger min = values.get(0);
      for (int i = 1; i < values.size(); ++i) {
        min = min.min(values.get(i));
      }
      return new Result(rp, ver, min);
    } else if (type_id == 3) { // maximum
      BigInteger max = values.get(0);
      for (int i = 1; i < values.size(); ++i) {
        max = max.max(values.get(i));
      }
      return new Result(rp, ver, max);
    } else if (type_id == 5) { // greater than
      int comp = values.get(0).compareTo(values.get(1));
      BigInteger res = (comp > 0) ? new BigInteger("1") : new BigInteger("0");
      return new Result(rp, ver, res);
    } else if (type_id == 6) { // less than
      int comp = values.get(0).compareTo(values.get(1));
      BigInteger res = (comp < 0) ? new BigInteger("1") : new BigInteger("0");
      return new Result(rp, ver, res);
    } else if (type_id == 7) { // equal to
      int comp = values.get(0).compareTo(values.get(1));
      BigInteger res = (comp == 0) ? new BigInteger("1") : new BigInteger("0");
      return new Result(rp, ver, res);
    }

    System.out.println("unexpected error occured");
    return new Result(rp, ver, new BigInteger("-1")); // error
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

    String packet = hex2bin(input);
    Result res = parse(packet, 0);
    System.out.println("Part 1: " + res.ver);
    System.out.println("Part 2: " + res.value);
  }
}
