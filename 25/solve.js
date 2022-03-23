const fs = require("fs");

if (process.argv.length !== 3) {
  console.log("Usage: node solve <input-file-name>");
} else {
  const fname = process.argv[2];
  const state = fs.readFileSync(fname, { encoding: "utf8" })
    .trim()
    .split("\n")
    .map(str => str.split(""));

  solve(state);
}

function solve(state) {
  const updates = [];

  let step = 0;
  while (true) {
    let changed = false;

    ++step;

    // get next state of east-facing cucumbers
    for (let y = 0; y < state.length; ++y) {
      for (let x = 0; x < state[0].length; ++x) {
        const nx = getRightPos(state, x);
        if (state[y][x] === ">" && state[y][nx] === ".") {
          updates.push([x, y, "."]);
          updates.push([nx, y, ">"]);
          changed = true;
        }
      }
    }

    // update
    for (const [x, y, c] of updates) {
      state[y][x] = c;
    }

    // get next state of south-facing cucumbers
    for (let y = 0; y < state.length; ++y) {
      for (let x = 0; x < state[0].length; ++x) {
        const ny = getBottomPos(state, y);
        if (state[y][x] === "v" && state[ny][x] === ".") {
          updates.push([x, y, "."]);
          updates.push([x, ny, "v"]);
          changed = true;
        }
      }
    }

    // update
    for (const [x, y, c] of updates) {
      state[y][x] = c;
    }

    if (!changed) {
      break;
    }
  }

  console.log(step);

  function getRightPos(state, x) {
    return (x < state[0].length-1) ? x+1 : 0;
  }

  function getBottomPos(state, y) {
    return (y < state.length-1) ? y+1 : 0;
  }

  // for debugging
  function print(state) {
    for (let y = 0; y < state.length; ++y) {
      console.log(state[y].join(""));
    }
  }
}
