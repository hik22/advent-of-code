const fs = require("fs");

if (process.argv.length !== 3) {
  console.log("Usage: node solve <input-file-name>");
} else {
  const fname = process.argv[2];
  const input = fs.readFileSync(fname, { encoding: "utf8" })
    .trim()
    .split("\n");

  console.log("Part 1: " + solvePart1(input));
  console.log("Part 2: " + solvePart2(input));
}

function solvePart1(input) {
  const roomSize = 2;
  const coords = getPossibleCoordinates(roomSize);
  const cache = new Map();

  const energies = organize(input, roomSize, coords, cache);
  return energies[0];
}

function solvePart2(input) {
  const roomSize = 4;
  const coords = getPossibleCoordinates(roomSize);
  const cache = new Map();

  // embed extra part in input
  const ninput = input.slice(0, 3)
    .concat(["  #D#C#B#A#", "  #D#B#A#C#"])
    .concat(input.slice(3));

  const energies = organize(ninput, roomSize, coords, cache);
  return energies[0];
}

// return possible coordinates for amphipods
function getPossibleCoordinates(roomSize) {
  const coords = [];

  // add hallway coordinates
  for (let x = 1; x < 12; ++x) {
    coords.push([x, 1]);
  }

  // add room coordinates
  for (const y of getRoomYs(roomSize)) {
    for (const x of [3, 5, 7, 9]) {
      coords.push([x, y]);
    }
  }

  return coords;
}

// return required energy to organize; if not possible to organize, return
// empty array
function organize(input, roomSize, coords, cache) {
  if (isOrganized(input, roomSize)) {
    return [0]; // no additional energy required
  }

  // simply return result if already calculated
  const hash = getHash(input, coords);
  if (cache.has(hash)) {
    return cache.get(hash);
  }

  // calculate energies to organize, if no matched cache
  let energies = [];
  for (const [x, y] of coords) {
    const type = input[y][x];
    if (type === ".") {
      continue;
    }

    // get energy for each possible move
    for (const dest of getPossibleDest(x, y, input, roomSize)) {
      // move
      const [dx, dy] = dest;
      moveAmphipod(x, y, dx, dy, input);

      // get result
      const e = getRequiredEnergy(x, y, dx, dy, type);
      const res = organize(input, roomSize, coords, cache);
      energies = energies.concat(res.map(n => n + e));

      // recover
      moveAmphipod(dx, dy, x, y, input);
    }
  }

  const res = (energies.length === 0) ? [] : [Math.min(...energies)];
  cache.set(hash, res);
  return res;
}

// return true if all amphipods are organized; otherwise, false
function isOrganized(input, roomSize) {
  const ys = [];
  for (let i = 0; i < roomSize; ++i) {
    ys.push(2+i);
  }

  for (const y of ys) {
    for (const x of [3, 5, 7, 9]) {
      const c = input[y][x];
      if (x === 3 && c !== "A") {
        return false;
      }
      if (x === 5 && c !== "B") {
        return false;
      }
      if (x === 7 && c !== "C") {
        return false;
      }
      if (x === 9 && c !== "D") {
        return false;
      }
    }
  }
  return true;
}

// return hach for input (amphipod positions); used as key for cache map
function getHash(input, coords) {
  const hash = [];
  for (const [x, y] of coords) {
    hash.push(input[y][x]);
  }
  return hash.join();
}

// return possible destinations for given amphipod's position
function getPossibleDest(x, y, input, roomSize) {
  const type = input[y][x];
  let r = { A: 3, B: 5, C: 7, D: 9 };

  if (y < 1 || y > 1+roomSize) {
    console.log(`bad y: ${y}`);
    return;
  }

  let res = [];
  if (y === 1 && isRoomEnterable(type, input, roomSize)) { // if in hallway
    const xend = r[type];
    const xbeg = (x > xend) ? x-1 : x+1;
    if (isHallwayEmpty(xbeg, xend, input)) {
      const ry = getEmptyRoomSpace(xend, input, roomSize);
      res.push([xend, ry]);
    }
  } else if (y > 1 && y <= 1+roomSize && 
    !isOrganizedInRoom(x, y, input, roomSize) &&
    isRoomEscapable(x, y, input)) { // if in room
    res = res.concat(getReachableHallway(x, input));
  }

  return res;
}

function moveAmphipod(x, y, dx, dy, input) {
  const c = input[y][x];
  const dc = input[dy][dx];

  input[dy] = input[dy].slice(0, dx) + c + input[dy].slice(dx+1);
  input[y] = input[y].slice(0, x) + dc + input[y].slice(x+1);
}

// return required energy to move amphipod
function getRequiredEnergy(x, y, dx, dy, type) {
  const moves = Math.abs(dx - x) + Math.abs(dy - y);
  const e = { A: 1, B: 10, C: 100, D: 1000 };

  return e[type] * moves;
}

// return true if there is no other amphipods above in the room for given
// amphipod
function isRoomEscapable(x, y, input) {
  for (let ry = y-1; ry > 1; --ry) {
    if (input[ry][x] !== ".") {
      return false;
    }
  }

  return true;
}

// return true if amphipod is organized so there is no need to move
function isOrganizedInRoom(x, y, input, roomSize) {
  const types = { "3": "A", "5": "B", "7": "C", "9": "D" };
  const type = types[x.toString()];

  // check if type is in correct place for its position and lower ones
  for (let ry = 1 + roomSize; ry >= y; --ry) {
    if (input[ry][x] !== type) {
      return false;
    }
  }

  return true;
}

// return hallway x positions for given x
function getReachableHallway(x, input) {
  const isStayable = x => (x !== 3 && x !== 5 && x !== 7 && x !== 9);
  const y = 1;

  let coords = [];

  // to right
  let hx = x;
  while (hx < 12) {
    ++hx;
    if (!isStayable(hx)) {
      continue;
    }
    if (input[y][hx] !== ".") { // if blocked
      break;
    }
    coords.push([hx, y]);
  }
  // to left
  hx = x;
  while (hx > 0) {
    --hx;
    if (!isStayable(hx)) {
      continue;
    }
    if (input[y][hx] !== ".") { // if blocked
      break;
    }
    coords.push([hx, y]);
  }

  return coords;
}

// return position of empty space as the highest possible y (so lowest in the
// diagram) in given room
function getEmptyRoomSpace(x, input, roomSize) {
  const ys = getRoomYs(roomSize).reverse(); // get y'x
  for (const y of ys) {
    if (input[y][x] === ".") {
      return y;
    }
  }
  console.log(`room is full: ${x}`);
  return;
}

// return true if empty between given positions
function isHallwayEmpty(x1, x2, input) {
  const y = 1;
  const xbeg = (x1 < x2) ? x1 : x2;
  const xend = (x1 < x2) ? x2 : x1;
  for (let x = xbeg; x <= xend; ++x) {
    const c = input[y][x];
    if (c !== ".") {
      return false;
    }
  }
  return true;
}

// return true if no other types are not in the room (that is, amphipod can
// enter the room, according to the problem's condition)
function isRoomEnterable(type, input, roomSize) {
  const ys = getRoomYs(roomSize); // get y'x
  let x = { A: 3, B: 5, C: 7, D: 9 }[type]; // get x

  // return false if other type is in the room
  for (const y of ys) {
    const c = input[y][x];
    if (c !== type && c !== ".") {
      return false;
    }
  }
  return true;
}

// return possible y coordinates array for room size
function getRoomYs(roomSize) {
  const ys = [];
  for (let i = 0; i < roomSize; ++i) {
    ys.push(2+i);
  }
  return ys;
}
