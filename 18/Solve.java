import java.util.*;
import java.io.*;

class Node {
  public Node parent;
  public Node left;
  public Node right;
  public int value;

  public Node() {
    parent = left = right = null;
    value = -1;
  }

  // for debugging
  public String toString() {
    if (left != null && right != null) {
      return "[" + left.toString() + ", " + right.toString() + "]";
    } else {
      return "" + value;
    }
  }
}

public class Solve {
  public static void main(String[] args) throws IOException {
    // exit if no file name given
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

    // solve part 1
    Node node1 = parse(input[0]);
    for (int i = 1; i < input.length; ++i) {
      Node node2 = parse(input[i]);
      node1 = add(node1, node2);
    }
    int mag = getMagnitude(node1);
    System.out.println("Part 1: " + mag);

    // solve part 2
    int max = 0;
    for (int i = 0; i < input.length; ++i) {
      for (int j = 0; j < input.length; ++j) {
        if (i == j) {
          continue;
        }

        // get magnitude of addition
        Node ln = parse(input[i]);
        Node rn = parse(input[j]);
        Node add = add(ln, rn);
        int res = getMagnitude(add);

        // update maximum
        if (res > max) {
          max = res;
        }
      }
    }
    System.out.println("Part 2: " + max);
  }

  // return Node object from string
  public static Node parse(String str) {
    Node root = new Node();
    Node current = root;
    for (int i = 0; i < str.length(); ++i) {
      char c = str.charAt(i);

      if (c == '[') {
        // add left child
        Node node = new Node();
        node.parent = current;
        current.left = node;

        // update current
        current = current.left;
      } else if (Character.isDigit(c)) {
        current.value = Character.digit(c, 10);
      } else if (c == ',') {
        current = current.parent;

        // add right child
        Node node = new Node();
        node.parent = current;
        current.right = node;

        // update current
        current = current.right;
      } else if (c == ']') {
        current = current.parent;
      }
    }
    return root;
  }

  public static Node add(Node l, Node r) {
    Node p = new Node();
    p.left = l;
    p.right = r;
    l.parent = p;
    r.parent = p;

    reduce(p);
    return p;
  }

  public static void reduce(Node root) {
    while (true) {
      Node n = findNodeToExplode(root, 0);
      if (n != null) { // found
        explode(n);
        continue;
      }

      n = findNodeToSplit(root);
      if (n != null) { // found
        split(n);
        continue;
      }

      break;
    }
  }

  public static Node findNodeToExplode(Node node, int depth) {
    if (depth == 4 && node.left != null && node.right != null) {
      return node;
    }

    Node res = null;
    if (node.left != null) {
      res = findNodeToExplode(node.left, depth+1);
    }
    if (res != null) {
      return res;
    }
    if (node.right != null) {
      res = findNodeToExplode(node.right, depth+1);
    }
    return res; // res remains null if not found
  }

  public static void explode(Node node) {
    int lval = node.left.value;
    int rval = node.right.value;

    // add each value to nearest nodes
    Node l = findNearestLeft(node);
    if (l != null) { // found
      l.value += lval;
    }
    Node r = findNearestRight(node);
    if (r != null) { // found
      r.value += rval;
    }

    // set current node as zero
    node.value = 0;
    node.left = null;
    node.right = null;
  }

  public static Node findNearestLeft(Node node) {
    while (true) {
      if (node.parent == null) {
        return null; // not found
      }

      if (node.parent.left == node) {
        // update
        node = node.parent;
      } else if (node.parent.right == node) {
        return getRightmostChild(node.parent.left);
      }
    }
  }

  public static Node findNearestRight(Node node) {
    while (true) {
      if (node.parent == null) {
        return null; // not found
      }

      if (node.parent.right == node) {
        // update
        node = node.parent;
      } else if (node.parent.left == node) {
        return getLeftmostChild(node.parent.right);
      }
    }
  }

  public static Node getRightmostChild(Node node) {
    while (node.right != null) {
      node = node.right;
    }
    return node;
  }

  public static Node getLeftmostChild(Node node) {
    while (node.left != null) {
      node = node.left;
    }
    return node;
  }

  public static Node findNodeToSplit(Node node) {
    if (node.value > 9) {
      return node;
    }

    Node res = null;
    if (node.left != null) {
      res = findNodeToSplit(node.left);
    }
    if (res != null) {
      return res;
    }
    if (node.right != null) {
      res = findNodeToSplit(node.right);
    }
    return res; // res remains null if not found
  }

  public static void split(Node node) {
    int val = node.value;
    int rem = val % 2;
    int lval = val / 2;
    int rval = val / 2 + rem;

    // make left child
    Node ln = new Node();
    ln.value = lval;
    ln.parent = node;
    node.left = ln;

    // make right child
    Node rn = new Node();
    rn.value = rval;
    rn.parent = node;
    node.right = rn;

    // prevent split
    node.value = 0;
  }

  public static int getMagnitude(Node node) {
    if (node.left != null && node.right != null) {
      return 3 * getMagnitude(node.left) + 2 * getMagnitude(node.right);
    }

    return node.value;
  }
}
