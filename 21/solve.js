const fs = require("fs");

if (process.argv.length !== 3) {
  console.log("Usage: node solve <input-file-name>");
} else {
  const fname = process.argv[2];
  const input = fs.readFileSync(fname, { encoding: "utf8" })
    .trim()
    .split("\n");

  // read player positions
  const pos = input
    .map(s => s.substring("Player 1 starting position: ".length))
    .map(s => parseInt(s));

  let copy = pos.slice();
  console.log("Part 1: " + solvePart1(copy));
  copy = pos.slice();
  console.log("Part 2: " + solvePart2(copy));
}

function solvePart1(pos) {
  class Dice {
    constructor() {
      this.value = 0;
    }
    roll() {
      if (this.value === 100) {
        this.value = 1;
      } else {
        this.value += 1;
      }
      return this.value;
    }
  }

  const score = [0, 0];

  const dice = new Dice();

  // play game and determine winner and loser
  let p = 0; // player
  let nrolls = 0; // number of rolling dice
  let loser, winner;
  while (true) {
    let moves = 0;
    for (let i = 0; i < 3; ++i) { // roll dice three times
      moves += dice.roll();
    }
    nrolls += 3; // count rolls

    // move position and increase scores
    pos[p] = getNextPosition(moves, pos[p]);
    score[p] += pos[p];

    if (score[p] >= 1000) {
      winner = p;
      loser = switchPlayer(p);
      break;
    }

    // switch player
    p = switchPlayer(p);
  }

  return score[loser] * nrolls;
}

function solvePart2(pos) {
  // dice value as key and frequency as value
  const rolls = { 3: 1, 4: 3, 5: 6, 6: 7, 7: 6, 8: 3, 9: 1 };

  const cache = new Map();

  const scores = [0, 0];
  const p = 0; // player
  const wins = roll(rolls, scores, pos, p, cache);

  // get winner's wins
  return Math.max(...wins);

  // return [number of player 1 wins, number of player 1 wins] array
  function roll(rolls, scores, pos, p, cache) {
    if (scores[0] >= 21) {
      return [1, 0]; // return 1 point for player 1
    }
    if (scores[1] >= 21) {
      return [0, 1]; // return 1 point for player 2
    }

    // return calculated result if it already is
    const hash = getHash(scores, pos, p);
    if (cache.has(hash)) {
      return cache.get(hash);
    }

    // calculate by recursion if no cache exists
    const wins = [0, 0];
    for (const [movesStr, freq] of Object.entries(rolls)) {
      const ppos = pos[p]; // previous position
      const pscore = scores[p]; // previous score

      // move player pawn and get score
      const moves = parseInt(movesStr);
      pos[p] = getNextPosition(moves, pos[p]);
      scores[p] += pos[p];

      // get number of wins
      const w = roll(rolls, scores, pos, switchPlayer(p), cache);
      wins[0] += freq * w[0];
      wins[1] += freq * w[1];

      // restore
      pos[p] = ppos;
      scores[p] = pscore;
    }
    cache.set(hash, wins); // save result into cache
    return wins;
  }

  function getHash(scores, pos, p) {
    return `${scores},${pos},${p}`;
  }
}

function switchPlayer(p) {
  return (p === 0) ? 1 : 0;
}

function getNextPosition(moves, pos) {
  return ((pos + moves) - 1) % 10 + 1;
}
