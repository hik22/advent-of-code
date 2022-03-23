const fs = require("fs");

if (process.argv.length !== 3) {
  console.log("Usage: node solve <input-file-name>");
} else {
  const fname = process.argv[2];
  const input = fs.readFileSync(fname, { encoding: "utf8" })
    .trim()
    .split("\n");

  solve(input);
}

function solve(input) {
  const nchunks = 14;
  const nchunk = 18;
  const params = [];
  for (let i = 0; i < nchunks; ++i) {
    const p = [];
    for (let j = 0; j < nchunk; ++j) {
      if (j === 4 || j === 5 || j === 15) {
        const line = input[i*nchunk+j];
        p.push(parseInt(line.split(" ")[2]));
      }
    }
    params.push(p);
  }
  params.reverse();

  let res = [[0, ""]];
  for (let p = 0; p < params.length; ++p) {
    const [a, b, c] = params[p];

    // get previous z and w
    const nres = [];
    for (const [nz, nw] of res) {
      for (let w = 1; w < 10; ++w) { // for each model number 1 to 9
        const zs = getPossibleZs(a, b, c, w, nz);
        for (const z of zs) {
          if (getNextZ(a, b, c, w, z) === nz) {
            nres.push([z, w + nw]);
          }
        }
      }
    }

    // update
    res = nres;
  }

  const modelnums = res.map(e => parseInt(e[1]));
  console.log("Part 1: " + Math.max(...modelnums));
  console.log("Part 2: " + Math.min(...modelnums));

  function getNextZ(a, b, c, w, z) {
    const x = (z % 26 + b) !== w ? 1 : 0;
    const nz = Math.floor(z / a) * (25 * x + 1) + (w + c) * x;
    return nz;
  }

  function getPossibleZs(a, b, c, w, nz) {
    const zs = new Set();

    // add z's for x = 1
    const factor = (nz - (w + c)) / 26;
    let zbeg = a * factor;
    let zend = a * (factor + 1);
    for (let z = zbeg; z < zend; ++z) {
      zs.add(z);
    }

    // add z's for x = 0
    zbeg = a * nz;
    zend = a * (nz + 1);
    for (let z = zbeg; z < zend; ++z) {
      zs.add(z);
    }

    return zs;
  }
}
