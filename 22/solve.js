const fs = require("fs");

// every end coordinate is past-the-end
class Box {
  constructor(xbeg, xend, ybeg, yend, zbeg, zend) {
    this.xbeg = xbeg;
    this.xend = xend;
    this.ybeg = ybeg;
    this.yend = yend;
    this.zbeg = zbeg;
    this.zend = zend;
  }

  asArray() {
    return [this.xbeg, this.xend, this.ybeg, this.yend, this.zbeg, this.zend];
  }
}

if (process.argv.length !== 3) {
  console.log("Usage: node solve <input-file-name>");
} else {
  const fname = process.argv[2];
  const input = fs.readFileSync(fname, { encoding: "utf8" })
    .trim()
    .split("\n");

  const regex = /^(on|off) x=(-?\d+)..(-?\d+),y=(-?\d+)..(-?\d+),z=(-?\d+)..(-?\d+)$/;
  console.log("Part 1: " + solvePart1(input, regex));
  console.log("Part 2: " + solvePart2(input, regex));
}

function solvePart1(input, regex) {
  const region = new Box(-50, 51, -50, 51, -50, 51);
  let boxes = [];
  for (const line of input) {
    const res = readInputLine(line, regex);
    const cmd = res.cmd;
    const intersection = intersect(res.box, region);

    if (intersection.empty) {
      continue;
    }
    const box = intersection.box;
    
    if (cmd === "on") {
      boxes = union(boxes, box);
    } else { // cmd === "off")
      boxes = subtract(boxes, box);
    }
  }

  let num = 0;
  for (const box of boxes) {
    num += getCubesNum(box);
  }
  return num;
}

function solvePart2(input, regex) {
  let boxes = [];
  for (const line of input) {
    const res = readInputLine(line, regex);
    console.log(res);
    const cmd = res.cmd;
    const box = res.box;

    if (cmd === "on") {
      boxes = union(boxes, box);
    } else { // cmd === "off")
      boxes = subtract(boxes, box);
    }
  }
  console.log(boxes);

  let num = 0;
  for (const box of boxes) {
    num += getCubesNum(box);
  }
  return num;
}

function readInputLine(line, regex) {
  // read command (on or off) and ranges
  const matches = line.match(regex).slice(1);
  const cmd = matches[0];
  const coords = matches.slice(1)
    .map(s => parseInt(s))
    .map((s, i) => (i % 2 === 0) ? s : s+1); // make end indices past-the-end

  return { box: new Box(...coords), cmd };
}

function union(target, box) {
  const boxes = subtract(target, box);
  boxes.push(box);
  return boxes;
}

function isEqualBox(box1, box2) {
  const arr1 = box1.asArray();
  const arr2 = box2.asArray();
  for (let i = 0; i < arr1.length; ++i) {
    if (arr1[i] !== arr2[i]) {
      return false;
    }
  }
  return true;
}

function getCubesNum(box) {
  let num = 0;
  const lx = box.xend - box.xbeg;
  const ly = box.yend - box.ybeg;
  const lz = box.zend - box.zbeg;
  return lx * ly * lz;
}

function subtract(target, box) {
  const boxes = [];
  for (const tbox of target) { // for each target box
    const intersection = intersect(box, tbox);

    if (intersection.empty) {
      // just push target box if no intersection
      boxes.push(tbox);
    } else {
      // split target box
      const ibox = intersection.box;
      const sboxes = splitBox(tbox, ibox);

      // push split box except intersection
      for (const sbox of sboxes) {
        if (!isEqualBox(sbox, ibox)) {
          boxes.push(sbox);
        }
      }
    }
  }
  return boxes;
}

function intersect(box1, box2) {
  const xbeg = Math.max(box1.xbeg, box2.xbeg);
  const xend = Math.min(box1.xend, box2.xend);
  const ybeg = Math.max(box1.ybeg, box2.ybeg);
  const yend = Math.min(box1.yend, box2.yend);
  const zbeg = Math.max(box1.zbeg, box2.zbeg);
  const zend = Math.min(box1.zend, box2.zend);

  if (xbeg < xend && ybeg < yend && zbeg < zend) {
    return { empty: false, box: new Box(xbeg, xend, ybeg, yend, zbeg, zend) };
  } else {
    return { empty: true, box: null };
  }
}

// assume small box is in large box
function splitBox(large, small) {
  const xranges = splitRange(large.xbeg, large.xend, small.xbeg, small.xend);
  const yranges = splitRange(large.ybeg, large.yend, small.ybeg, small.yend);
  const zranges = splitRange(large.zbeg, large.zend, small.zbeg, small.zend);

  const boxes = [];
  for (const xr of xranges) {
    for (const yr of yranges) {
      for (const zr of zranges) {
        const box = new Box(xr.beg, xr.end, yr.beg, yr.end, zr.beg, zr.end);
        boxes.push(box);
      }
    }
  }
  return boxes;
}

// assume [sbeg, send] is in [lbeg, lend]
function splitRange(lbeg, lend, sbeg, send) {
  const ranges = [];
  if (lbeg < sbeg) {
    ranges.push({ beg: lbeg, end: sbeg});
  }
  ranges.push({ beg: sbeg, end: send});
  if (send < lend) {
    ranges.push({ beg: send, end: lend});
  }

  return ranges;
}
